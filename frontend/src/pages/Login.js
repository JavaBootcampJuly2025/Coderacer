import { login } from "../services/apiService";
import { useState } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useTheme } from '../styles/ThemeContext';

function Login() {
    const { theme } = useTheme(); // Get current theme
    const [message, setMessage] = useState("");
    const [messageColor, setMessageColor] = useState("");

    const submit = async (e) => {
        e.preventDefault();

        const form = e.target;
        const loginData = {
            username: form.username.value,
            password: form.password.value,
        };

        try {
            const response = await login(loginData);
            setMessageColor("text-green-400");
            setMessage("Login successful. Redirecting to home");
            localStorage.setItem('loginToken', response.token);
            localStorage.setItem('loginId', response.id);

            setTimeout(() => {
                window.location.href = "/";
            }, 1000);
        } catch (error) {
            setMessageColor("text-red-400");
            setMessage(error.response?.data?.message || "Login failed.");
        }
    };

    return (
        <div className={`min-h-screen flex flex-col items-center font-montserrat transition-colors duration-300 ${
            theme === 'light' ? 'bg-white text-black' : 'bg-[#13223A] text-white'
        }`}>
            {/* Header */}
            <div className="w-full h-[80px] border border-[#59000000]">
                <Header />
            </div>

            {/* Register Form */}
            <div className="flex-grow flex items-center justify-center py-12 w-full max-w-2xl">
                <div className={`w-full p-10 rounded-2xl shadow-xl transition-colors duration-300 ${
                    theme === 'light' ? 'bg-gray-100' : 'bg-[#1C2B47]'
                }`}>
                    <h2 className="text-2xl font-semibold mb-6 text-center text-[var(--accent)]">Login Into Your Account</h2>
                    <form onSubmit={submit} className="space-y-6">
                        <input
                            className="w-full p-3 rounded-xl bg-[var(--input-bg)] text-[var(--text)] placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[var(--accent)]"
                            type="text"
                            name="username"
                            placeholder="Enter your username"
                            required
                        />
                        <input
                            className="w-full p-3 rounded-xl bg-[var(--input-bg)] text-[var(--text)] placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-[var(--accent)]"
                            type="password"
                            name="password"
                            placeholder="Enter your password"
                            required
                        />
                        <button
                            type="submit"
                            className="w-full py-3 rounded-xl bg-[var(--primary-button)] text-[var(--primary-button-text)] font-semibold hover:bg-[var(--primary-button-hover)] transition duration-300"
                        >
                            Login
                        </button>
                    </form>
                    {message && (
                        <p className={`mt-4 text-center ${messageColor}`}>{message}</p>
                    )}
                </div>
            </div>

            {/* Footer */}
            <div className="w-full h-[80px] border border-[#59000000]">
                <Footer />
            </div>
        </div>
    );
}

export default Login;