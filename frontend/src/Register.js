import { createAccount } from './services/apiService';

function Register() {
  const submit = async e => {
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

    }
  };

  return (
    <>
      <div className="content">
        <h1 className="header">Register</h1>
        <form onSubmit={submit}>
          <label className="label" htmlFor="email">Enter Email</label><br></br>
          <input className="inputForm" type="text" id="email" name="email" placeholder="Email" /><br/>
          <label className="label" htmlFor="username">Enter Username</label><br></br>
          <input className="inputForm" type="text" id="username" name="username" placeholder="Username" /><br/>
          <label className="label" htmlFor="password">Enter Password</label><br></br>
          <input className="inputForm" type="password" id="password" name="password" placeholder="Password" /><br/>
          <input className="action" type="submit" value="Register" />
        </form> 
      </div>
    </>
  );
}

export default Register;
