import React, { useState } from 'react';
import NotLoggedInPopup from './NotLoggedInPopup';
import {deleteAccount, changePassword} from "../services/apiService";

const ProfilePopup = ({ loggedOn, updateAccountInfo }) => {
    const [currentOverlay, setCurrentOverlay] = useState(null);
    const [message, setMessage] = useState("");
    const [messageColor, setMessageColor] = useState("");

    const handleChangePasswordClick = () => {
        setCurrentOverlay("changePassword");
    };

    const handleDeleteClick = () => {
        setCurrentOverlay("delete");
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

    const confirmChangePassword = async (e) => {
        e.preventDefault();

        const form = e.target;
        const passwordChangeData = {
            currentPassword: form.currentPassword.value,
            newPassword: form.newPassword.value,
        };

        console.log(passwordChangeData);

        const token = localStorage.getItem('loginToken');
        const id = localStorage.getItem('loginId');

        try {
            const response = await changePassword(id, passwordChangeData, token);
            setMessageColor("text-green-400");
            setMessage("Password changed successfully.");
            setTimeout(() => {
                setCurrentOverlay(null);
            }, 1000);
        } catch (error) {
            setMessageColor("text-red-400");
            setMessage(error.response?.data?.message || "Password change failed.");
        }
    };

    const cancelAction = () => {
        setCurrentOverlay(null);
    };

    if (loggedOn === false) return <NotLoggedInPopup />;

    return (
        <>
            {(currentOverlay === "changePassword") && (
                <div onClick={(e) => e.stopPropagation()} className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-30">
                    <div className="bg-white dark:bg-[var(--background)] text-black dark:text-[var(--text)] rounded-2xl p-6 shadow-lg max-w-sm w-full">
                        <h2 className="text-xl font-semibold mb-4">Change Password</h2>
                        <form onSubmit={confirmChangePassword} className="space-y-4">
                            <div>
                                <label className="block text-sm mb-1" htmlFor="currentPassword">Current Password</label>
                                <input
                                    type="password"
                                    id="currentPassword"
                                    className="w-full px-4 py-2 border rounded-xl bg-gray-100 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm mb-1" htmlFor="newPassword">New Password</label>
                                <input
                                    type="password"
                                    id="newPassword"
                                    className="w-full px-4 py-2 border rounded-xl bg-gray-100 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                />
                            </div>
                            <div className="flex justify-end gap-4 pt-4">
                                <button
                                    type="button"
                                    onClick={cancelAction}
                                    className="px-4 py-2 rounded-xl bg-gray-300 dark:bg-gray-700 text-black dark:text-white hover:bg-gray-400 dark:hover:bg-gray-600"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="px-4 py-2 rounded-xl bg-blue-600 text-white hover:bg-blue-700"
                                >
                                    Confirm
                                </button>
                            </div>
                        </form>
                        {message && (
                            <p className={`mt-4 text-center ${messageColor}`}>{message}</p>
                        )}
                    </div>
                </div>
            )}


            {(currentOverlay === "delete") && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-30">
                    <div className="bg-white dark:bg-[var(--background)] text-black dark:text-[var(--text)] rounded-2xl p-6 shadow-lg max-w-sm w-full">
                        <h2 className="text-xl font-semibold mb-4">Confirm Deletion</h2>
                        <p className="mb-6">Are you sure you want to delete your account?</p>
                        <div className="flex justify-end gap-4">
                            <button
                                onClick={cancelAction}
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
                    onClick={handleChangePasswordClick}
                >
                    Change Password
                </button>
            </div>
        </>
    );
};

export default ProfilePopup;
