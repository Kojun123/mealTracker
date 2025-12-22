import { useState, useEffect } from "react";
import dayjs from "dayjs";

function App() {
  const [input, setInput] = useState('')
  const [logs, setLogs] = useState([])
  const [summary, setSummary] = useState(null)
  const [items, setItems] = useState([])

  const loadSummary = async () => {
    const res = await fetch("/api/meal/getSummary", {
      method: 'POST',
      headers: {'Content-Type': 'application/json'}
    });
    const data = await res.json();
    setSummary(data.todaySummary);
    setItems(data.items ?? []);
  }

  useEffect(() => {
    // 화면 첫 진입시 실행
    loadSummary();
  }, []);

  const send = async () => {
      const text = input.trim()
      if(!text) return

      setLogs((prev) => [
        ...prev,
        {role: 'user', text}
      ])

      setInput('')

      const res = await fetch('/api/meal/message', {
        method: 'POST',
        headers: {'Content-Type': 'application/json' },
        body: JSON.stringify({message:text})
      })

      const data = await res.json()
      console.log('data',data)

      setLogs((prev) => [
        ...prev,
        {role: 'assistant', text: data.assistantText}
      ])
      setSummary(data.todaySummary)
      setItems(data.items ?? [])
  }

  return (
    <div style={{ padding: 40, maxWidth: 600}}>
      <h1>Meal Tracker</h1>

    {items.length > 0 && (
    <div
      style={{
        border: '1px solid #ddd',
        borderRadius: 8,
        padding: 12,
        marginBottom: 16
      }}
    >

    <h3 style={{ marginTop: 0 }}>오늘 먹은 것</h3>

   {summary && (
  <div
    style={{
      display: 'grid',
      gridTemplateColumns: '2fr 1fr 2fr 1fr 4fr',
      gap: 10,
      marginBottom: 12,
      padding: '8px 0',
      fontWeight: 700
    }}
  >
    <div>오늘 합계</div>
    <div>-</div>
    <div>{Math.round(summary.totalCalories)} kcal</div>
    <div>{Math.round(summary.totalProtein)} g</div>
    <div>-</div>
  </div>
)}


    <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr 1fr 1fr 4fr', gap: 10, fontWeight: 700 }}>
      <div>음식</div>
      <div>수량</div>
      <div>칼로리</div>
      <div>단백질</div>
      <div>시간</div>
    </div>

    <div style={{ marginTop: 8 }}>
      {items.map((it, idx) => (
        <div
          key={idx}
          style={{
            display: 'grid',
            gridTemplateColumns: '2fr 1fr 1fr 1fr 4fr',
            gap: 10,
            padding: '6px 0',
            borderTop: '1px solid #eee'
          }}
        >
          <div>{it.name}</div>
          <div>x{it.count}</div>
          <div>{Math.round(it.calories)}</div>
          <div>{Math.round(it.protein)}</div>
          <div>{it.createdAt ? dayjs(it.createdAt).format("YYYY-MM-DD HH:mm") : "-"} </div>
          <div>{/*new Date(it.createdAt).toLocaleString("ko-KR")*/}</div>
        </div>
      ))}
    </div>
  </div>
)}



      <div style={{display: 'flex', gap: 8 }}>
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}

          onKeyDown={(e) => {
            if(e.key === 'Enter') {
              send()
            }
          }}

          placeholder="ex: 오늘 식단 시작"
          style={{flex: 1, padding: 8}}
        />

        <button onClick={send}>
         전송
        </button>
      </div>

        <div style={{marginTop: 20, whiteSpace: "pre-line"}}>
          {logs.map((log, idx) => (
            <div key={idx} style={{marginBottom: 8}}>
              <b>
                {log.role === 'user' ? '나' : 'GPT'}:
              </b>{' '}

              {log.text}
              </div>
          ))}
        </div>
    </div>
  )
}

export default App
