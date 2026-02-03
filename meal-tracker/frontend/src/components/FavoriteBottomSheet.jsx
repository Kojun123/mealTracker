import { useMemo, useState } from "react";
import { BottomSheet } from "./BottomSheet";
import { apiFetch } from "../lib/apiFetch";

function toNumberOrNull(v) {
  const n = Number(v);
  if (!Number.isFinite(n)) return null;
  return n;
}

function Row({ item, onDelete }) {
  return (
    <div className="flex items-center justify-between rounded-2xl border border-white/10 bg-white/5 px-4 py-3">
      <div className="min-w-0">
        <div className="truncate text-sm font-semibold text-slate-100">
          {item.name}
        </div>

        <div className="mt-1 flex gap-2 text-xs text-slate-400">
          <span>칼로리:  {item.calories ?? "-"}  kcal</span>
          <span>단백질:  {item.protein ?? "-"} g</span>
        </div>
      </div>

      <button
        type="button"
        onClick={() => onDelete(item.id)}
        className="shrink-0 rounded-xl px-3 py-1.5 text-xs text-rose-200 hover:bg-rose-400/10"
      >
        삭제
      </button>
    </div>
  );
}

export default function FavoriteBottomSheet({ open, onClose, favorites, setFavorites, reloadFavorites }) {
  const [name, setName] = useState("");
  const [calories, setCalories] = useState("");
  const [protein, setProtein] = useState("");

  const canAdd = useMemo(() => name.trim().length > 0, [name]);

  const addFavorite = async () => {
    if (!canAdd) return;

    const item = {
      id: crypto?.randomUUID ? crypto.randomUUID() : String(Date.now()),
      name: name.trim(),
      calories: calories.trim() ? toNumberOrNull(calories) : null,
      protein: protein.trim() ? toNumberOrNull(protein) : null,
    };

    await apiFetch("/api/meal/favorite", {
      method: "POST",      
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(item),      
    }).catch(console.error);    
    await reloadFavorites();

    setName("");
    setCalories("");
    setProtein("");
  };

  const deleteFavorite = async (id) => {
    await apiFetch(`/api/meal/favorite`, {
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id }),
      method: "DELETE",                                                                                                                       
    }).catch(console.error);
    await reloadFavorites();
  };
  

  return (
    <BottomSheet open={open} title="자주 먹는 것" onClose={onClose}>
      
      <div className="space-y-2">
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="음식 이름  ex) xx 닭가슴살"
          className={[
            "h-11 w-full rounded-2xl px-4 text-sm outline-none",
            "border border-white/10 bg-white/5 text-slate-100",
            "placeholder:text-slate-400",
            "focus:border-sky-300/60 focus:ring-4 focus:ring-sky-400/15",
          ].join(" ")}
        />

        <div className="grid grid-cols-2 gap-2">
          <input
            value={calories}
            onChange={(e) => setCalories(e.target.value)}
            inputMode="numeric"
            placeholder="칼로리"
            className={[
              "h-11 w-full rounded-2xl px-4 text-sm outline-none",
              "border border-white/10 bg-white/5 text-slate-100",
              "placeholder:text-slate-400",
              "focus:border-sky-300/60 focus:ring-4 focus:ring-sky-400/15",
            ].join(" ")}
          />
          <input
            value={protein}
            onChange={(e) => setProtein(e.target.value)}
            inputMode="numeric"
            placeholder="단백질"
            className={[
              "h-11 w-full rounded-2xl px-4 text-sm outline-none",
              "border border-white/10 bg-white/5 text-slate-100",
              "placeholder:text-slate-400",
              "focus:border-sky-300/60 focus:ring-4 focus:ring-sky-400/15",
            ].join(" ")}
          />
        </div>

        <button
          type="button"
          onClick={addFavorite}
          disabled={!canAdd}
          className={[
            "h-11 w-full rounded-2xl text-sm font-semibold transition",
            "border border-white/10",
            canAdd
              ? "bg-white/5 text-slate-100 hover:bg-white/10"
              : "cursor-not-allowed bg-white/10 text-slate-500",
          ].join(" ")}
        >
          추가
        </button>
      </div>
      
      <div className="mt-4">
        <div className="mb-2 text-xs font-semibold text-slate-200">등록된 목록</div>

        <div className="max-h-[40vh] overflow-y-auto space-y-2 pr-1">
          {(favorites ?? []).length === 0 ? (
            <div className="rounded-2xl border border-white/10 bg-white/5 p-4 text-xs text-slate-400">
              아직 등록된 게 없어요.
            </div>
          ) : (
            (favorites ?? []).map((x) => (
              <Row key={x.id} item={x} onDelete={deleteFavorite} />
            ))
          )}
        </div>
      </div>
    </BottomSheet>
  );
}
