#!/usr/bin/env bash
set -u
BASE=${BASE:-http://localhost:8080/api}
COOKIES=$(mktemp)
FAILED=0

check() {
  if [ "$2" = "$3" ]; then
        echo "PASS $1 ($3)"
    else 
        echo "FAIL $1 - expect $2, receive $3"
        FAILED=$((FAILED + 1))
    fi
}

code() { echo "$1" | tail -n1; }

# 1. Register (First 201, Duplicate 409)
S=$(curl -s -o /dev/null -w "%{http_code}" -X POST $BASE/auth/register \
    -H "Content-Type: application/json" \
  -d '{"email":"test@teamflow.dev", "password":"test1234","fullname":"Tester"}')
if [ "$S" = "201" ] || [ "$S" = "409" ]; then
    echo "PASS register ($S)"
else
    echo "FAIL register - receive $S"; FAILED=$((FAILED + 1))
fi

# 2. Wrong password
S=$(curl -s -o /dev/null -w "%{http_code}" -X POST $BASE/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@teamflow.dev", "password":"wrong"}')
check "login wrong password" 401 "$S"

#3. Login success
BODY=$(curl -s -c "$COOKIES"  -X POST $BASE/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@teamflow.dev", "password":"test1234"}')
TOKEN=$(printf '%s' "$BODY" | node -e "let body=''; process.stdin.on('data', chunk => body += chunk); process.stdin.on('end', () => { try { process.stdout.write(JSON.parse(body).accessToken || ''); } catch (_) {} });")
[ -n "$TOKEN" ] && echo "PASS login get token" || { echo "FAIL login not get token" ; FAILED=$((FAILED + 1)); }

# 4. Không token
S=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/users/me")
check "users/me không token" 401 "$S"

# 5. Có token
S=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/users/me" \
  -H "Authorization: Bearer $TOKEN")
check "users/me có token" 200 "$S"

# 6. Token giả
S=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/users/me" \
  -H "Authorization: Bearer khong-phai-token")
check "token giả" 401 "$S"

# 7. Refresh
echo "KIỂM CHỨNG TOÀN BỘ LUỒNG AUTH"

S=$(curl -s -o /dev/null -w "%{http_code}" \
  -b "$COOKIES" \
  -X POST "$BASE/auth/refresh")
check "refresh" 200 "$S"

# 8. Logout
S=$(curl -s -o /dev/null -w "%{http_code}" \
  -b "$COOKIES" \
  -c "$COOKIES" \
  -X POST "$BASE/auth/logout")
check "logout" 204 "$S"

# 9. Refresh sau logout
S=$(curl -s -o /dev/null -w "%{http_code}" \
  -b "$COOKIES" \
  -X POST "$BASE/auth/refresh")
check "refresh sau logout" 401 "$S"

rm -f "$COOKIES"

echo "===================="
[ "$FAILED" -eq 0 ] && echo "TẤT CẢ PASS" || echo "$FAILED bước thất bại"

exit "$FAILED"
