export default function StatsCards({summary, user, itemsCount}) {
    const totalCalories = summary ? Math.round(summary.totalCalories) : 0;
    const totalProtein = summary ? Math.round(summary.totalProtein) : 0;

    const targetCalories = user ? Math.round(user.targetCalories ?? 0) : 0;
    const targetProtein = user ? Math.round(user.targetProtein ?? 0) : 0;

    return (
        <section className="mt-6 grid grid-cols-1 gap-3 sm:grid-cols-3">
            <div className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
                <div className="text-sm text-gray-500">칼로리</div>
                <div className="mt-1 text-3xl font-semibold text-gray-900">
                    {totalCalories} / {targetCalories}
                    <span className="ml-2 text-base font-medium text-gray-500">kcal</span>
                </div>
            </div>

            <div className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
                <div className="text-sm text-gray-500">단백질</div>
                <div className="mt-1 text-3xl font-semibold text-gray-900">
                    {totalProtein} / {targetProtein}
                    <span className="ml-2 text-base font-medium text-gray-500">g</span>
                </div>
            </div>

            <div className="rounded-2xl border border-gray-100 bg-white p-4 shadow-sm">
                <div className="text-sm text-gray-500">기록</div>
                <div className="mt-1 text-3xl font-semibold text-gray-900">
                 {itemsCount}
                 <span className="ml-2 text-base font-medium text-gray-500">items</span>
                </div>
            </div>

        </section>
    );
}