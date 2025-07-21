import { createAccount } from '../services/apiService';
import { useState } from 'react';

function Register() {
  const [message, setMessage] = useState("");
  const [messageColor, setMessageColor] = useState("");
  
  const submit = async (e) => {
    e.preventDefault();

    const form = e.target;
    const accountData = {
      email: form.email.value,
      username: form.username.value,
      password: form.password.value,
    };

    try {
      const response = await createAccount(accountData);
      setMessageColor("green");
      setMessage("registration succesful, check your email to verify your account and then login");
    } catch (error) {
      setMessageColor("red");
      setMessage(error.response?.data?.message);
    }
  };

  return (
      <div className="home-wrapper">
        <div className="content-glass">
          <h1 className="header">Register</h1>
          <form onSubmit={submit}>
            <input
                className="inputForm"
                type="email"
                id="email"
                name="email"
                placeholder="Enter your email"
                required
            />
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
            <input className="action" type="submit" value="Register" />
            <text className="message" style={{color: messageColor}}>{message}</text>
          </form>
        </div>


      </div>
  );
}

export default Register;