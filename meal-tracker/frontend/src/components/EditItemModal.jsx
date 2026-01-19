import dayjs from "dayjs";

export default function EditItemModal({ open, item, setItem, onClose, onSubmit }) {
  if (!open) return null;

  const update = (patch) => setItem((prev) => ({ ...(prev ?? {}), ...patch }));

  const name = item?.name ?? "";
  const count = item?.count ?? 1;
  const calories = item?.calories ?? 0;
  const protein = item?.protein ?? 0;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      
      <div className="absolute inset-0 bg-black/30" onClick={onClose} />

      
      <div className="relative w-full max-w-md rounded-2xl bg-white p-5 shadow-lg">
        <div className="flex items-start justify-between gap-3">
          <div>
            <div className="text-base font-semibold text-gray-900">기록 수정</div>
            <div className="mt-1 text-xs text-gray-500">
              {item?.createdAt ? dayjs(item.createdAt).format("YYYY-MM-DD HH:mm") : ""}
            </div>
          </div>

          <button
            onClick={onClose}
            className="rounded-lg px-2 py-1 text-sm text-gray-500 hover:bg-gray-100"
            aria-label="close"
          >
            ✕
          </button>
        </div>

        <div className="mt-4 space-y-3">
      
          <div>
            <div className="mb-1 text-xs font-semibold text-gray-700">음식명</div>
            <input
              value={name}
              onChange={(e) => update({ name: e.target.value })}
              className="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm outline-none focus:border-gray-400"
              placeholder="예: 셀렉스 프로핏"
            />
          </div>

          
          <div>
            <div className="mb-1 text-xs font-semibold text-gray-700">수량</div>
            <input
              type="number"
              min={1}
              value={count}
              onChange={(e) => update({ count: Number(e.target.value) })}
              className="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm outline-none focus:border-gray-400"
            />
          </div>

          
          <div className="grid grid-cols-2 gap-3">
            <div>
              <div className="mb-1 text-xs font-semibold text-gray-700">칼로리</div>
              <input
                type="number"
                min={0}
                value={calories}
                onChange={(e) => update({ calories: Number(e.target.value) })}
                className="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm outline-none focus:border-gray-400"
              />
            </div>

            <div>
              <div className="mb-1 text-xs font-semibold text-gray-700">단백질</div>
              <input
                type="number"
                min={0}
                value={protein}
                onChange={(e) => update({ protein: Number(e.target.value) })}
                className="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm outline-none focus:border-gray-400"
              />
            </div>
          </div>

          <div className="mt-2 rounded-xl bg-gray-50 px-3 py-2 text-xs text-gray-600">
            값은 총합 기준으로 입력해야 합니다. (수량이 반영된 값)
          </div>
        </div>

        <div className="mt-5 flex justify-end gap-2">
          <button
            onClick={onClose}
            className="rounded-xl border border-gray-200 bg-white px-4 py-2 text-sm font-semibold text-gray-700 hover:bg-gray-50"
          >
            취소
          </button>

          <button
            onClick={onSubmit}
            className="rounded-xl bg-gray-900 px-4 py-2 text-sm font-semibold text-white hover:bg-gray-800"
          >
            저장
          </button>
        </div>
      </div>
    </div>
  );
}
