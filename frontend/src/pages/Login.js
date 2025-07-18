function Login() {
    const submit = async (e) => {
        e.preventDefault();
    };

    return (
        <div className="home-wrapper">
            <div className="content-glass">
                <h1 className="header">Login</h1>
                <form onSubmit={submit}>
                    {/*<label className="label" htmlFor="email">*/}
                    {/*    Email*/}
                    {/*</label>*/}
                    <input
                        className="inputForm"
                        type="email"
                        id="email"
                        name="email"
                        placeholder="Enter your email"
                        required
                    />
                    {/*<label className="label" htmlFor="password">*/}
                    {/*    Password*/}
                    {/*</label>*/}
                    <input
                        className="inputForm"
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Enter your password"
                        required
                    />



                    <input className="action" type="submit" value="Login" />
                </form>
            </div>
        </div>
    );
}

export default Login;