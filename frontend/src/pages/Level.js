import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState, useRef } from "react";
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer, ReferenceDot, } from "recharts";
let Lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
function Level() {
    const { state } = useLocation();
    const navigate = useNavigate();
    const [codeSnippet, setCodeSnippet] = useState("testingtesting");
    const [userInput, setUserInput] = useState("");
    const [startTime, setStartTime] = useState(null);
    const [endTime, setEndTime] = useState(null);
    const [totalTyped, setTotalTyped] = useState(0);
    const [mistakes, setMistakes] = useState(0);
    const [speedLog, setSpeedLog] = useState([]);
    const containerRef = useRef(null);

    useEffect(() => {
        if (containerRef.current) {
            containerRef.current.focus();
            const range = document.createRange();
            const selection = window.getSelection();
            if (containerRef.current.firstChild) {
                range.setStart(containerRef.current.firstChild, 0);
                range.collapse(true);
                selection.removeAllRanges();
                selection.addRange(range);
            }
        }
    }, []);

    const handleKeyDown = (e) => {
        if (endTime) return;

        if (e.key.length === 1 || e.key === "Backspace" || e.key === "Enter" || e.key === "Tab") {
            e.preventDefault();

            const now = Date.now();
            const elapsedSec = startTime ? (now - startTime) / 1000 : 0;
            let newInput = userInput;

            if (!startTime && userInput.length === 0) {
                setStartTime(now);
            }

            if (e.key === "Backspace") {
                newInput = userInput.slice(0, -1);
            } else {
                if (e.key === "Enter") {
                    newInput = userInput + "\n";
                } else if (e.key === "Tab") {
                    newInput = userInput + "  ";
                } else {
                    newInput = userInput + e.key;
                }

                setTotalTyped((prev) => prev + (e.key === "Tab" ? 2 : 1));

                const expectedChar = codeSnippet[userInput.length];
                const actualChar = e.key === "Enter" ? "\n" : e.key === "Tab" ? "  " : e.key;
                const isMistake = actualChar !== expectedChar && e.key !== "Backspace";

                if (isMistake) {
                    setMistakes((prev) => prev + 1);
                }
                else {
                    const correctChars = [...newInput].filter((ch, i) => ch === codeSnippet[i]).length;
                    const cpm = elapsedSec > 0 ? Math.round((correctChars / elapsedSec) * 60) : 0;

                    setSpeedLog((prev) => [...prev, { time: (elapsedSec).toFixed(1), cpm }]);
                }
            }

            setUserInput(newInput);

            if (newInput === codeSnippet) {
                setEndTime(now);
            }
        }
    };

    const calculateCPM = () => {
        if (!startTime || !endTime) return 0;
        const duration = (endTime - startTime) / 1000 / 60;
        return Math.round(codeSnippet.length / duration);
    };

    const calculateAccuracy = () => {
        if (totalTyped === 0) return 100;
        return Math.max(0, Math.round(((totalTyped - mistakes) / totalTyped) * 100));
    };

    useEffect(() => {
        containerRef.current?.focus();
    }, []);

    return (
        <div
            tabIndex={0}
            ref={containerRef}
            onKeyDown={handleKeyDown}
            style={{
                padding: "2rem",
                fontFamily: "monospace",
                outline: "none",
                minHeight: "100vh",
                backgroundColor: "#f9f9f9",
            }}
        >
            <h2>Typing Speed Test</h2>

            <div
                style={{
                    padding: "1rem",
                    marginTop: "1rem",
                    background: "#fff",
                    border: "2px solid #ccc",
                    borderRadius: "8px",
                    whiteSpace: "pre-wrap",
                    wordWrap: "break-word",
                    fontSize: "16px",
                    lineHeight: "1.5",
                    minHeight: "200px",
                    cursor: "text",
                }}
                onClick={() => containerRef.current?.focus()}
            >
                {codeSnippet.split("").map((char, idx) => {
                    let color = "#888";
                    if (idx < userInput.length) {
                        color = userInput[idx] === char ? "green" : "red";
                    }
                    return (
                        <span key={idx} style={{ color }}>{char}</span>
                    );
                })}
                {userInput.length < codeSnippet.length && (
                    <span
                        style={{
                            background: "#000",
                            width: "1px",
                            display: "inline-block",
                            animation: "blink 1s step-start 0s infinite",
                        }}
                    >
            &nbsp;
          </span>
                )}
            </div>

            {endTime && (
                <div style={{ marginTop: "1rem", fontWeight: "bold" }}>
                    Test Complete! <br />
                    <strong>CPM:</strong> {calculateCPM()} <br />
                    <strong>Accuracy:</strong> {calculateAccuracy()}%<br />
                    <strong>Keystrokes:</strong> {totalTyped} | Mistakes: {mistakes}
                </div>
            )}
            {endTime && speedLog.length > 1 && (
                <div style={{ marginTop: "2rem" }}>
                    <h3>Typing Speed Over Time</h3>
                    <ResponsiveContainer width="100%" height={300}>
                        <LineChart data={speedLog}>
                            <CartesianGrid stroke="#eee" strokeDasharray="5 5" />
                            <XAxis dataKey="time" label={{ value: "Time (s)", position: "insideBottom", dy: 10 }} />
                            <YAxis label={{ value: "CPM", angle: -90, position: "insideLeft" }} />
                            <Tooltip />
                            <Line type="monotone" dataKey="cpm" stroke="#8884d8" strokeWidth={2} dot={false} />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            )}

            <style>{`
        @keyframes blink {
          50% { background: transparent; }
        }
      `}</style>
        </div>
    );
}

export default Level;