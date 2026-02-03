import { useMemo, useRef, useState } from "react";

const Token = ({ token, onRemove }) => (
  <span
    className={[
      "inline-flex items-center gap-2 rounded-full px-3 py-1.5 text-xs",
      "border border-white/10 bg-white/5 text-slate-100",
      "whitespace-nowrap",
    ].join(" ")}
  >
    <span className="max-w-[160px] truncate">{token.name} ({token.calories}kcal, {token.protein}g) </span>
    <span className="rounded-full border border-white/10 bg-white/5 px-2 py-0.5 text-[11px] text-slate-200">
      ×{token.qty}
    </span>
    <button
      type="button"
      onClick={onRemove}
      className="rounded-full px-1.5 py-0.5 text-slate-300 hover:bg-white/10 hover:text-slate-100"
      aria-label="remove"
      title="삭제"
    >
      ×
    </button>
  </span>
);

const Chip = ({ label, onClick, tone = "normal" }) => (
  <button
    type="button"
    onClick={onClick}
    title={label}
    className={[
      "inline-flex items-center gap-1 rounded-full px-3 py-1.5 text-xs",
      "border border-white/10 bg-white/5 text-slate-100",
      "hover:bg-white/10 active:scale-[0.98] transition",
      "whitespace-nowrap max-w-[240px] truncate",
      tone === "muted" ? "text-slate-300" : "",
      tone === "add" ? "border-sky-300/30 text-sky-200 hover:bg-sky-400/10" : "",
    ].join(" ")}
  >
    <span className="opacity-90">+</span>
    <span className="truncate">{label}</span>
  </button>
);

export default function Composer({
  input,
  setInput,
  onSend,
  loading,
  recentItems = [],
  favorites = [],
  onOpenFavoritesSheet,
}) {
  // 수량 토큰
  const [tokens, setTokens] = useState([]); // [{ name, qty }]
  const textRef = useRef(null);

  const addToken = (token) => {
    const n = (token.name ?? "").trim();
    if (!n) return;

    setTokens((prev) => {
      const idx = prev.findIndex((t) => t.name === n);      
      if (idx >= 0) {
        const next = prev.slice();
        next[idx] = { ...next[idx], qty: (next[idx].qty ?? 1) + 1 };
        return next;
      }      
      return [{...token, qty:1}, ...prev];
    });

    requestAnimationFrame(() => textRef.current?.focus?.());
  };

  const removeTokenById = (id) => {
    setTokens((prev) => prev.filter((t) => t.id !== id));
    requestAnimationFrame(() => textRef.current?.focus?.());
  };

  const decLastToken = () => {
    setTokens((prev) => {
      if (prev.length === 0) return prev;
      const lastIdx = prev.length - 1;
      const last = prev[lastIdx];

      if (last.qty > 1) {
        const next = prev.slice();
        next[lastIdx] = { ...last, qty: last.qty - 1 };
        return next;
      }
      return prev.slice(0, -1);
    });
  };

  
  const combinedText = useMemo(() => {
    const t = (input ?? "").trim();

    const tokenText = tokens
      .slice()
      .reverse()
      .map((x) => (`${x.name} x${x.qty} (단백질 : ${x.protein}g) (${x.calories}kcal)`))
      .join(", ");

    if (!tokenText) return t;
    if (!t) return tokenText;
    return `${tokenText}, ${t}`;
  }, [tokens, input]);

  const handleSend = () => {
    const text = combinedText.trim();
    if (!text || loading) return;

    onSend(text);
    
    setTokens([]);
    setInput("");
  };

  return (
    <div className="border-t border-white/10 px-4 py-4 sm:px-5">
      {/* 최근 먹은 것 */}
      {recentItems.length > 0 && (
        <div className="mb-3">
          <div className="mb-2 text-xs font-semibold text-slate-200">최근 먹은 것</div>
          <div className="flex gap-2 overflow-x-auto pb-1">
            {recentItems.map((x) => (
              <Chip key={`recent-${x}`} label={x} tone="muted" onClick={() => addToken(x)} />
            ))}
          </div>
        </div>
      )}

      {/* 자주 먹는 것 */}
      <div className="mb-3">
        <div className="mb-2 flex items-center justify-between">
          <div className="text-xs font-semibold text-slate-200">자주 먹는 것</div>
          <button
            type="button"
            onClick={onOpenFavoritesSheet}
            className="text-[11px] text-slate-400 hover:text-slate-200 transition"
          >
            관리
          </button>
        </div>

        <div className="flex gap-2 overflow-x-auto pb-1">
          {favorites.map((x) => (
            <Chip
              key={`fav-${x.id}`}
              label={x.name}
              onClick={() => addToken(x)}
            />
          ))}
          <Chip label="추가" tone="add" onClick={onOpenFavoritesSheet} />
        </div>
      </div>

      {/* 입력창 */}
      <div className="flex gap-2">
        <div
          onClick={() => textRef.current?.focus?.()}
          className={[
            "min-h-11 flex-1 rounded-2xl px-3 py-2",
            "border border-white/10 bg-white/5 text-slate-100",
            "shadow-[inset_0_1px_0_rgba(255,255,255,0.08)]",
            "focus-within:border-sky-300/60 focus-within:ring-4 focus-within:ring-sky-400/15",
            "flex flex-wrap items-center gap-2",
          ].join(" ")}
        >
          {tokens.map((t) => (
            <Token
              token = {t}
              onRemove={() => removeTokenById(t.id)}
            />
          ))}

          <input
            ref={textRef}
            value={input}
            onChange={(e) => {              
              setInput(e.target.value);              
            }}
            onKeyDown={(e) => {
              if (e.isComposing) return;

              if (e.key === "Backspace") {
                if ((input ?? "").length === 0 && tokens.length > 0) {
                  e.preventDefault();
                  decLastToken();
                }
              }

              if (e.key === "Enter") {
                e.preventDefault();
                handleSend();
              }
            }}
            placeholder={tokens.length === 0 ? "ex) 계란 2개, 닭가슴살 1개" : ""}
            className={[
              "h-7 min-w-[120px] flex-1 bg-transparent text-sm outline-none",
              "placeholder:text-slate-400",
            ].join(" ")}
          />
        </div>

        <button
          onClick={handleSend}
          disabled={loading}
          type="button"
          className={[
            "h-11 shrink-0 rounded-2xl px-4 text-sm font-semibold transition",
            "border border-white/10",
            loading
              ? "cursor-not-allowed bg-white/10 text-slate-400"
              : "bg-white/5 text-slate-100 hover:bg-white/10",
          ].join(" ")}
        >
          {loading ? "전송 중..." : "전송"}
        </button>
      </div>
    </div>
  );
}
