import React from 'react';

const ProfilePopup = ({ username, rating, email, avgCpm, avgAccuracy, loggedOn }) => {
    if(loggedOn === false) return (
        <div
            className="absolute rounded-2xl shadow-lg p-4 z-20 border"
            style={{
                top: '6rem',
                right: '0.5rem',
                backgroundColor: 'var(--background)',
                color: 'var(--text)',
                borderColor: 'var(--accent)',
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
        </div>
    )
    return (
        <div
            className="absolute rounded-2xl shadow-lg p-4 z-20 border"
            style={{
                top: '6rem',
                right: '0.5rem',
                backgroundColor: 'var(--background)',
                color: 'var(--text)',
                borderColor: 'var(--accent)',
            }}
        >
            <div
                className="font-semibold text-lg mb-3 border-b pb-2"
                style={{
                    borderColor: 'var(--accent)',
                    color: 'var(--accent)',
                }}
            >
                User Info
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>Username:</span> {username || 'PlaceholderUser'}
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>Rating:</span> {rating || 0}
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>Email:</span> {email || '<EMAIL>'}
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>AvgCpm:</span> {avgCpm || 0}
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>AvgAccuracy:</span> {avgAccuracy || 0}
            </div>
        </div>
    );
};

export default ProfilePopup;
