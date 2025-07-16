function Login() {
    const submit = async e => {
      e.preventDefault();
    };
  return (
    <>
      <div className="content">
        <h1 className="header">Login</h1>
        <form onSubmit={submit}>
          <label className="label" htmlFor="email">Enter Email</label><br></br>
          <input className="inputForm" type="text" id="email" name="email" placeholder="Email" /><br/>
          <label className="label" htmlFor="password">Enter Password</label><br></br>
          <input className="inputForm" type="password" id="password" name="password" placeholder="Password" /><br/>
          <input className="action" type="submit" value="Login" />
        </form>
      </div>
    </>
  );
}

export default Login;
