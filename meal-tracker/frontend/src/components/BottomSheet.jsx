import { useEffect } from "react";

export function BottomSheet({ open, title, children, onClose }) {
  useEffect(() => {
    if (!open) return;

    const onKeyDown = (e) => {
      if (e.key === "Escape") onClose?.();
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50">
      {/* dim */}
      <button
        type="button"
        aria-label="닫기"
        onClick={onClose}
        className="absolute inset-0 bg-black/40"
      />

      {/* sheet */}
      <div
        className={[
          "absolute left-0 right-0 bottom-0",
          "rounded-t-3xl border border-white/35 bg-slate-950/20",
          "shadow-[0_-20px_60px_rgba(0,0,0,0.55)]",
          "backdrop-blur",
        ].join(" ")}
      >
        <div className="mx-auto w-full max-w-xl">
          {/* handle + header */}
          <div className="px-5 pt-3">
            <div className="mx-auto mb-3 h-1.5 w-12 rounded-full bg-white/15" />
            <div className="flex items-center justify-between">
              <div className="text-sm font-semibold text-slate-100">{title}</div>
              <button
                type="button"
                onClick={onClose}
                className="rounded-xl px-3 py-1.5 text-xs text-slate-300 hover:bg-white/5"
              >
                닫기
              </button>
            </div>
          </div>

          <div className="px-5 pb-5 pt-4">{children}</div>
        </div>
      </div>
    </div>
  );
}
