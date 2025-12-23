import { useState, useEffect } from "react";
import dayjs from "dayjs";

function App() {
  const [input, setInput] = useState('')
  const [logs, setLogs] = useState([])
  const [summary, setSummary] = useState(null)
  const [items, setItems] = useState([])
  const [session, setSession] = useState(null)

  const loadDashBoard = async () => {
    const res = await fetch("/api/meal/today", {
      method: 'POST',
      headers: {'Content-Type': 'application/json'}
    });
    const data = await res.json();
    console.log('today',data);
    setSummary(data.todaySummary);
    setItems(data.items ?? []);
    setSession(data.session);
  }

  useEffect(() => {
    // 화면 첫 진입시 실행
    loadDashBoard();
  }, []);

    const sendPreset = async (presetText) => {
    const text = presetText.trim();
    if (!text) return;
    await sendText(text);
  }

  const sendText = async (text) => {
  setLogs((prev) => [...prev, { role: "user", text }]);
  setInput("");

  const res = await fetch("/api/meal/message", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ message: text }),
  });

  if (!res.ok) {
    setLogs((prev) => [...prev, { role: "assistant", text: "서버 오류" }]);
    return;
  }

  const data = await res.json();

  setLogs((prev) => [...prev, { role: "assistant", text: data.assistantText }]);
  setSummary(data.todaySummary);
  setItems(data.items ?? []);
};

const send = async () => {
  const text = input.trim();
  if (!text) return;
  await sendText(text);
};

//기록 시작/중단/재개 버튼 함수
const startSession = async () => {
  await fetch("/api/meal/session/start", { method: "POST" });
  reloadSession();
};

const pauseSession = async () => {
  await fetch("/api/meal/session/pause", { method: "POST" });
  reloadSession();
};

const resumeSession = async () => {
  await fetch("/api/meal/session/resume", { method: "POST" });
  reloadSession();
};

const reloadSession = async () => {
  const res = await fetch("/api/meal/session/today");
  const data = await res.json();
  setSession(data.session);
};



  const isActive = session?.status === "ACTIVE";
  const isPaused = session?.status === "PAUSED";
  const isClosed = session?.status === "CLOSED";

  return (
    <div style={{ padding: 40, maxWidth: 600}}>
      <h1>Meal Tracker</h1>
      
    <div
  style={{
    border: "1px solid #eee",
    borderRadius: 10,
    padding: 12,
    marginBottom: 16,
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  }}
>
  <div style={{ fontWeight: 700 }}>
    {session
      ? `${isActive ? "🟢" : isPaused ? "⏸" : "⚪"} ${session.statusText}`
      : "⚪ 기록 없음"}
  </div>

 <div style={{ display: "flex", gap: 8, marginBottom: 20 }}>
  <button
    onClick={startSession}
    disabled={isActive}
    style={{ opacity: isActive ? 0.4 : 1 }}
  >
    기록 시작
  </button>

  <button
    onClick={pauseSession}
    disabled={!isActive}
    style={{ opacity: !isActive ? 0.4 : 1 }}
  >
    기록 중단
  </button>

  <button
    onClick={resumeSession}
    disabled={!isPaused}
    style={{ opacity: !isPaused ? 0.4 : 1 }}
  >
    기록 재개
  </button>
</div>

</div>



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
