import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState, useRef } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
  ReferenceDot,
} from "recharts";

function Level() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [codeSnippet, setCodeSnippet] = useState("");
  const [userInput, setUserInput] = useState("");
  const [startTime, setStartTime] = useState(null);
  const [endTime, setEndTime] = useState(null);
  const [totalTyped, setTotalTyped] = useState(0);
  const [mistakes, setMistakes] = useState(0);
  const [mistakeIndices, setMistakeIndices] = useState([]);
  const [speedLog, setSpeedLog] = useState([]);
  const containerRef = useRef(null);

  useEffect(() => {
    if (!state?.level?.codeSnippet) {
      navigate("/");
    } else {
      setCodeSnippet(state.level.codeSnippet.trim());
    }
  }, [state, navigate]);

  const handleKeyDown = (e) => {
    if (endTime) return;

    if (e.key.length === 1 || e.key === "Backspace" || e.key === "Enter" || e.key === "Tab") {
      e.preventDefault();

      const now = Date.now();
      const elapsedSec = startTime ? (now - startTime) / 1000 : 0;
      let newInput = userInput;

      // Start timer
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

        const charsTyped = newInput.length;
        const cpm = elapsedSec > 0 ? Math.round((charsTyped / elapsedSec) * 60) : 0;

        setSpeedLog((prev) => [...prev, { time: (elapsedSec).toFixed(1), cpm }]);
        setTotalTyped((prev) => prev + (e.key === "Tab" ? 2 : 1));

        const expectedChar = codeSnippet[userInput.length];
        const actualChar = e.key === "Enter" ? "\n" : e.key === "Tab" ? "  " : e.key;
        const isMistake = actualChar !== expectedChar && e.key !== "Backspace";

        if (isMistake) {
          setMistakes((prev) => prev + 1);
          setMistakeIndices((prev) => [...prev, (elapsedSec).toFixed(1)]);
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
          ✅ Test Complete! <br />
          🚀 <strong>CPM:</strong> {calculateCPM()} <br />
          🎯 <strong>Accuracy:</strong> {calculateAccuracy()}%<br />
          ✏️ <strong>Keystrokes:</strong> {totalTyped} | ❌ Mistakes: {mistakes}
        </div>
      )}

      {/* Graph */}
      {endTime && speedLog.length > 1 && (
        <div style={{ marginTop: "2rem" }}>
          <h3>📈 Typing Speed Over Time</h3>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={speedLog}>
              <CartesianGrid stroke="#eee" strokeDasharray="5 5" />
              <XAxis dataKey="time" label={{ value: "Time (s)", position: "insideBottom", dy: 10 }} />
              <YAxis label={{ value: "CPM", angle: -90, position: "insideLeft" }} />
              <Tooltip />
              <Line type="monotone" dataKey="cpm" stroke="#8884d8" strokeWidth={2} dot={false} />

              {mistakeIndices.map((t, i) => (
                <ReferenceDot key={i} x={t} y={null} r={5} fill="red" stroke="none" label="" />
              ))}
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
