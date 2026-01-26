import { DayPicker } from "react-day-picker";
import "react-day-picker/dist/style.css";
import dayjs from "dayjs";
import { useEffect, useRef, useState } from "react";

export default function DatePopover({ selectedDate, setSelectedDate }) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const onDown = (e) => {
      if (!ref.current) return;
      if (!ref.current.contains(e.target)) setOpen(false);
    };
    if (open) document.addEventListener("mousedown", onDown);
    return () => document.removeEventListener("mousedown", onDown);
  }, [open]);

  const selected = dayjs(selectedDate).toDate();

  return (
    <div className="relative" ref={ref}>
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className="inline-flex items-center gap-2 rounded-xl border border-gray-200 bg-white px-3 py-2 text-sm font-semibold text-gray-800 shadow-sm hover:bg-gray-50"
      >
        <span className="text-gray-400">📅</span>
        <span>{dayjs(selectedDate).format("YYYY.MM.DD")}</span>
        <span className="text-xs font-medium text-gray-500">
          ({dayjs(selectedDate).format("dd")})
        </span>
      </button>

      {open && (
        <div 
          className="fixed inset-0 z-20 flex items-center justify-center p-3 sm:absolute sm:inset-auto sm:right-0 sm:mt-2"
          onMouseDown={() => setOpen(false)}
        >
          <div
           className="w-[min(22rem,calc(100vw-1.5rem))] select-none rounded-2xl border border-gray-200 bg-white p-3 shadow-lg"
            onMouseDown={(e) => e.stopPropagation()}
          >
          <DayPicker
            mode="single"
            selected={selected}
            onSelect={(d) => {
              if (!d) return;
              setSelectedDate(dayjs(d).format("YYYY-MM-DD"));
              setOpen(false);
            }}
          />
          </div>
        </div>
      )}
    </div>
  );
}
