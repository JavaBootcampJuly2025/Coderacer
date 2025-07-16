import './Home.css'
import logo from './resources/thumbsup.png';

function Home() {
  return (
    <>
      <div className="content">
        <h1 className="header">Coderacer</h1>
        <img className="logo" src={logo} />
        <br />
        <a href="/login">
          <button className="action">Login</button>
        </a>
        <br />
        <a href="/register">
           <button className="action">Register</button>
        </a>
      </div>
    </>
  );
}

export default Home;
