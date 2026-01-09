export default function DashboardHeader({user, onOpenGoal, onLogout}) {
    return (
        <header className="flex items-center justify-between">
            <div>
                <h1 className="text-2xl font-semibold text-gray-900">Meal Tracker</h1>
                <p className="mt-1 text-sm text-gray-500">먹은 거 대충 던지면 기록해주는 앱</p>
            </div>

            <div className="flex items-center gap-4">
                {user?.email && (
                    <span className="text-sm font-medium text-gray-700">
                        {user.email.split("@")[0]} 님
                    </span>
                )}

                <button
                    className="flex items-center gap-1 rounded-xl border border-gray-200 bg-white px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
                    onClick={onOpenGoal}
                >
                    <span>⚙</span>
                    <span>목표 설정</span>
                </button>

                <button
                    onClick={onLogout}
                    className="rounded-xl border border-gray-200 bg-white px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                    로그아웃
                </button>
            </div>
        </header>
    );
}