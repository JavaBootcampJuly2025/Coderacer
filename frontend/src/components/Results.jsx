import React from 'react';

const Results = ({ endTime, calculateCPM, calculateAccuracy, totalTyped, mistakes }) => {
    if (!endTime) return null;

    return (
        <div style={{ marginTop: '1rem', fontWeight: 'bold' }}>
            Test Complete! <br />
            <strong>CPM:</strong> {calculateCPM()} <br />
            <strong>Accuracy:</strong> {calculateAccuracy()}%<br />
            <strong>Keystrokes:</strong> {totalTyped} | Mistakes: {mistakes}
        </div>
    );
};

export default Results;