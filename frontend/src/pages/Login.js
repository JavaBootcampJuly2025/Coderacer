import { login } from "../services/apiService";
import { useState } from 'react';

function Login() {
    const [message, setMessage] = useState("");
    const [messageColor, setMessageColor] = useState("");

    const submit = async (e) => {
        e.preventDefault();

        const form = e.target;
        const loginEnter = {
            username: form.username.value,
            password: form.password.value,
        };

        try {
            const response = await login(loginEnter);
            setMessageColor("green");
            setMessage("login succesful redirecting to home");
            localStorage.setItem('loginToken', response.token);
            localStorage.setItem('loginId', response.id);

            setTimeout(() => {
                window.location.href = "/";
            }, 1000);
        } catch (error) {
            setMessageColor("red");
            setMessage(error.response?.data?.message);
        }
    };
    return (
        <div className="home-wrapper">
            <div className="content-glass">
                <h1 className="header">Login</h1>
                <form onSubmit={submit}>
                    <input
                        className="inputForm"
                        type="text"
                        id="username"
                        name="username"
                        placeholder="Enter your username"
                        required
                    />
                    <input
                        className="inputForm"
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Enter your password"
                        required
                    />
                    <input className="action" type="submit" value="Login" />
                    <Text className="message" style={{color: messageColor}}>{message}</Text>
                </form>
            </div>
        </div>
    );
}

export default Login;