export default function Compose({input, setInput, onSend}) {
    return (
        <div className="border-t border-gray-100 px-5 py-4">
            <div className="flex gap-2">
                <input
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyDown={(e) => {
                        if (e.key === "Enter") onSend();
                    }}
                    placeholder="ex) 계란 2개, 닭가슴살 1개"
                    className="flex-1 rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm outline-none focus:border-gray-400"
                />                
                <button
                    onClick={onSend}
                    className="rounded-xl bg-gray-900 px-4 py-3 text-sm font-semibold text-white hover:bg-gray-800"
                >
                    전송
                </button>
            </div>
        </div>
    )
}