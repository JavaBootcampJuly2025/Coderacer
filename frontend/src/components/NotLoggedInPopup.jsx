import React from 'react';

const NotLoggedInPopup = () => {
    return (
        <div
            className="absolute rounded-2xl shadow-lg p-4 z-20 border break-words"
            style={{
                top: '6rem',
                right: '0.5rem',
                backgroundColor: 'var(--background)',
                color: 'var(--text)',
                borderColor: 'var(--accent)',
                width: '20rem',
            }}
        >
            <div
                className="font-semibold text-lg mb-3 border-b pb-2"
                style={{
                    borderColor: 'var(--accent)',
                    color: 'var(--accent)',
                }}
            >
                Not Logged In
            </div>
            <button
                className="w-full py-3 rounded-xl bg-[var(--primary-button)] text-[var(--primary-button-text)] font-semibold hover:bg-[var(--primary-button-hover)] transition duration-300"
                onClick={() => window.location.href = '/login'}
            >
                Login
            </button>
            <p className="text-base text-[var(--text)] leading-relaxed overflow-y-auto">
                Don't a have an account? <a href="/register" className="underline">Register</a> here.
            </p>
        </div>
    );
};

export default NotLoggedInPopup;
