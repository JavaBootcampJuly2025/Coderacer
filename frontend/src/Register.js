import { createAccount } from './services/apiService';

function Register() {
  const submit = async (e) => {
    e.preventDefault();

    const form = e.target;
    const accountData = {
      email: form.email.value,
      username: form.username.value,
      password: form.password.value,
    };

    try {
      const result = await createAccount(accountData);
    } catch (error) {
      // Handle error appropriately (e.g., display error message)
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
          </form>
        </div>
      </div>
  );
}

export default Register;