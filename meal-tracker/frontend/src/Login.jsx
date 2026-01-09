import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setErr(null);
    setLoading(true);

    try {
      const res = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({ email, password }),
        credentials: "include",
      });

      console.log("status", res.status);
      console.log("headers", [...res.headers.entries()]);
      console.log("text", await res.text());

      if (res.status === 200) {
        navigate("/");
        return;
      }

      setErr("이메일 또는 비밀번호가 틀림");
    } catch {
      setErr("네트워크 오류");
    } finally {
      setLoading(false);
    }
  };

  return (
  <div className="min-h-screen bg-gray-50">
    <div className="mx-auto flex min-h-screen max-w-5xl items-center justify-center px-6 py-10">
      <div className="w-full max-w-md">
        <div className="rounded-3xl border border-gray-100 bg-white p-8 shadow-sm">
          <div className="text-2xl font-semibold text-gray-900">Meal Tracker</div>
          <div className="mt-1 text-sm text-gray-500">대충 기록하는 식단</div>

          <form className="mt-8 space-y-5" onSubmit={submit}>
            <div>
              <label className="block text-sm font-medium text-gray-700">이메일</label>
              <input
                className="mt-2 w-full rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm outline-none focus:border-gray-400"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                autoComplete="email"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">비밀번호</label>
              <input
                className="mt-2 w-full rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm outline-none focus:border-gray-400"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                autoComplete="current-password"
              />
            </div>

            {err && (
              <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
                {err}
              </div>
            )}

            <button
              className={[
                "w-full rounded-xl px-4 py-3 text-sm font-semibold",
                loading
                  ? "bg-gray-200 text-gray-500 cursor-not-allowed"
                  : "bg-gray-900 text-white hover:bg-gray-800",
              ].join(" ")}
              type="submit"
              disabled={loading}
            >
              {loading ? "로그인 중..." : "로그인"}
            </button>

            <div className="pt-2 text-center text-sm text-gray-600">
              {" "}
              <button
                type="button"
                className="font-semibold text-gray-900 hover:underline"
                onClick={() => alert("회원가입은 아직. 인간의 욕망은 무한하지만 개발시간은 유한함.")}
              >
                회원가입
              </button>
            </div>
          </form>
        </div>

        <div className="mt-6 text-center text-xs text-gray-500">
          ---
        </div>
      </div>
    </div>
  </div>
);

}
