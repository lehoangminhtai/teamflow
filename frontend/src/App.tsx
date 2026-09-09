type StatusRowProps = {
  label: string;
  ok: boolean;
}

function StatusRow({label, ok} : StatusRowProps){
  return(
    <div className="flex items-center gap-2 text-sm text-slate-700">
      <span className={`size-2 rounded-full ${ok ? 'bg-emerald-500' : 'bg-slate-50'}`}/>
      <span>{label}</span>
    </div>
  )
}

function App() {
   return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6">
      <div className="max-w-md w-full rounded-xl border border-slate-200 bg-white p-8 shadow-sm">
        <p className="text-xs font-semibold uppercase tracking-widest text-teal-700">TeamFlow</p>
        <h1 className="mt-2 text-2xl font-bold text-slate-900"> Frontend đã chạy</h1>
        <div className="mt-5 space-y-2 border-t border-slate-100 pt-4">
          <StatusRow label="Database (Docker, cong 5432)" ok/>
          <StatusRow label="Backend (Spring Boot, cong 8080)" ok/>
          <StatusRow label="Frontend (Vite, cong 5173)" ok/>
        </div>
      </div>
    </div>    
  )
}

export default App
