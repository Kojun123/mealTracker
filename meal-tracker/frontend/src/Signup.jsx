import {useState} from "react";
import {useNavigate, Link} from "react-router-dom"
import { apiFetch } from "./lib/apiFetch";

export default function Signup() {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [password2, setPassword2] = useState("");    
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState(null);

    const submit = async (e) => {
        e.preventDefault()
        setErr(null);

        const e1 = email.trim();
        if (!e1) return setErr("이메일을 입력하세요");
        if (!password) return setErr("비밀번호를 입력하세요.");
        if (password.length < 8) return setErr("비밀번호는 8자 이상이어야 합니다.");
        if (password !== password2) return setErr("비밀번호가 일치하지 않습니다.");
        
        setLoading(true);
        try {
            const res = await apiFetch("/api/auth/signup", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: e1, password }),
                credentials: "include",
            });

            if (res.status === 409) {
                setErr("이미 존재하는 이메일입니다.");
                return;
            }

            if (!res.ok) {
                throw new Error("네트워크 오류");
                return ;
            }
            navigate("/login", {replace:true});
        } finally {
            setLoading(false);
        }
    };

    return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <form
        onSubmit={submit}
        className="w-full max-w-sm rounded-2xl border border-gray-100 bg-white p-6 shadow-sm"
      >
        <div className="text-2xl font-semibold text-gray-900">회원가입</div>        

        <label className="mt-6 block text-sm font-medium text-gray-700">이메일</label>
        <input
          className="mt-2 w-full rounded-xl border border-gray-200 px-4 py-3 text-sm outline-none focus:border-gray-400"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="you@example.com"
          autoComplete="email"
        />

        <label className="mt-4 block text-sm font-medium text-gray-700">비밀번호</label>
        <input
          className="mt-2 w-full rounded-xl border border-gray-200 px-4 py-3 text-sm outline-none focus:border-gray-400"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="8자 이상"
          autoComplete="new-password"
        />

        <label className="mt-4 block text-sm font-medium text-gray-700">비밀번호 확인</label>
        <input
          className="mt-2 w-full rounded-xl border border-gray-200 px-4 py-3 text-sm outline-none focus:border-gray-400"
          type="password"
          value={password2}
          onChange={(e) => setPassword2(e.target.value)}
          placeholder="한 번 더"
          autoComplete="new-password"
        />

        {err && (
          <div className="mt-4 rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
            {err}
          </div>
        )}

        <button
          type="submit"
          disabled={loading}
          className={[
            "mt-5 w-full rounded-xl px-4 py-3 text-sm font-semibold text-white",
            loading ? "bg-gray-300" : "bg-gray-900 hover:bg-gray-800",
          ].join(" ")}
        >
          {loading ? "가입 중..." : "가입하기"}
        </button>

        <div className="mt-5 text-center text-sm text-gray-600">
          이미 계정이 있다면 → {" "}
          <Link to="/login" className="font-medium text-gray-900 hover:underline">
            로그인
          </Link>
        </div>
      </form>
    </div>
  );
}