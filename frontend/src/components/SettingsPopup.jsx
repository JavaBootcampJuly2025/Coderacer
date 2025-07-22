import React, { useState } from 'react';
import NotLoggedInPopup from './NotLoggedInPopup';
import {deleteAccount} from "../services/apiService";

const ProfilePopup = ({ loggedOn, updateAccountInfo }) => {
    const [showConfirm, setShowConfirm] = useState(false);

    const handleDeleteClick = () => {
        setShowConfirm(true);
    };

    const confirmDelete = async () => {
        const token = localStorage.getItem('loginToken');
        const id = localStorage.getItem('loginId');

        try {
            const response = await deleteAccount(id, token);
        } catch (error) {

        }

        localStorage.removeItem('loginToken');
        localStorage.removeItem('loginId');

        updateAccountInfo();
    };

    const cancelDelete = () => {
        setShowConfirm(false);
    };

    if (loggedOn === false) return <NotLoggedInPopup />;

    return (
        <>
            {showConfirm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-30">
                    <div className="bg-white dark:bg-[var(--background)] text-black dark:text-[var(--text)] rounded-2xl p-6 shadow-lg max-w-sm w-full">
                        <h2 className="text-xl font-semibold mb-4">Confirm Deletion</h2>
                        <p className="mb-6">Are you sure you want to delete your account?</p>
                        <div className="flex justify-end gap-4">
                            <button
                                onClick={cancelDelete}
                                className="px-4 py-2 rounded-xl bg-gray-300 dark:bg-gray-700 text-black dark:text-white hover:bg-gray-400 dark:hover:bg-gray-600"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={confirmDelete}
                                className="px-4 py-2 rounded-xl bg-red-600 text-white hover:bg-red-700"
                            >
                                Yes, Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}

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
                    Settings
                </div>
                <button
                    className="w-full py-3 rounded-xl bg-[var(--primary-button)] text-[var(--primary-button-text)] font-semibold hover:bg-[var(--primary-button-hover)] transition duration-300"
                    onClick={handleDeleteClick}
                >
                    Delete Account
                </button>
                <button
                    className="w-full py-3 rounded-xl bg-[var(--primary-button)] text-[var(--primary-button-text)] font-semibold hover:bg-[var(--primary-button-hover)] transition duration-300"
                    onClick={handleDeleteClick}
                >
                    Change Password
                </button>
            </div>
        </>
    );
};

export default ProfilePopup;
