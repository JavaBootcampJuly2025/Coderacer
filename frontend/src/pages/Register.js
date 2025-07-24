import { createAccount } from '../services/apiService';
import { useState } from 'react';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { useTheme } from '../styles/ThemeContext';

const CONFIG = {
  root: {
    minHeight: 'min-h-screen',
    backgroundColor: 'bg-[var(--background)]',
    flexDirection: 'flex-col',
    alignItems: 'items-center',
    fontFamily: 'font-montserrat',
    textColor: 'text-[var(--text)]',
  },
  header: {
    width: 'w-full',
    height: 'h-[80px]'
  },
  main: {
    flex: 'flex-grow',
    justifyContent: 'justify-center',
    alignItems: 'items-center',
    padding: 'py-12',
    width: 'w-full',
    maxWidth: 'max-w-2xl',
  },
  card: {
    width: 'w-full',
    padding: 'p-10',
    borderRadius: 'rounded-2xl',
    shadow: 'shadow-xl',
    transition: 'transition-colors duration-300',
    border: 'border border-[var(--border-gray)]',
    backgroundColor: 'bg-[var(--inbetween)]',
  },
  form: {
    gap: 'space-y-6',
  },
  input: {
    width: 'w-full',
    padding: 'p-3',
    borderRadius: 'rounded-xl',
    backgroundColor: 'bg-[var(--sliderhover)]',
    textColor: 'text-[var(--text)]',
    placeholder: 'placeholder-[var(--placeholder)]',
    focus: 'focus:outline-none focus:ring-2 focus:ring-[var(--accent)]',
  },
  button: {
    width: 'w-full',
    padding: 'py-3',
    borderRadius: 'rounded-xl',
    backgroundColor: 'bg-[var(--primary-button)]',
    textColor: 'text-[var(--primary-button-text)]',
    fontWeight: 'font-semibold',
    hover: 'hover:bg-[var(--primary-button-hover)]',
    transition: 'transition duration-300',
  },
  title: {
    fontSize: 'text-2xl',
    fontWeight: 'font-semibold',
    margin: 'mb-6',
    textAlign: 'text-center',
    textColor: 'text-[var(--accent)]',
  },
  message: {
    margin: 'mt-4',
    textAlign: 'text-center',
  },
};

function Register() {
  const { theme, themeConfig } = useTheme();
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
      await createAccount(accountData);
      setMessageColor("text-[var(--success-message)]");
      setMessage("Registration successful. Check your email to verify your account.");
    } catch (error) {
      setMessageColor("text-[var(--error-message)]");
      setMessage(error.response?.data?.message || "Registration failed.");
    }
  };

  return (
      <div className={`home-wrapper ${CONFIG.root.minHeight} flex ${CONFIG.root.flexDirection} ${CONFIG.root.alignItems} ${CONFIG.root.fontFamily} ${CONFIG.root.backgroundColor} ${CONFIG.root.textColor}`}>
        <div className={`${CONFIG.header.width} ${CONFIG.header.height} ${CONFIG.header.border}`}>
          <Header />
        </div>

        <div className={`${CONFIG.main.flex} flex ${CONFIG.main.justifyContent} ${CONFIG.main.alignItems} ${CONFIG.main.padding} ${CONFIG.main.width} ${CONFIG.main.maxWidth}`}>
          <div className={`${CONFIG.card.width} ${CONFIG.card.padding} ${CONFIG.card.borderRadius} ${CONFIG.card.shadow} ${CONFIG.card.transition} ${CONFIG.card.border} ${CONFIG.card.backgroundColor}`}>
            <h2 className={`${CONFIG.title.fontSize} ${CONFIG.title.fontWeight} ${CONFIG.title.margin} ${CONFIG.title.textAlign} ${CONFIG.title.textColor}`}>Create Your Account</h2>
            <form onSubmit={submit} className={`${CONFIG.form.gap}`}>
              <input
                  className={`${CONFIG.input.width} ${CONFIG.input.padding} ${CONFIG.input.borderRadius} ${CONFIG.input.backgroundColor} ${CONFIG.input.textColor} ${CONFIG.input.placeholder} ${CONFIG.input.focus}`}
                  type="email"
                  name="email"
                  placeholder="Enter your email"
                  required
              />
              <input
                  className={`${CONFIG.input.width} ${CONFIG.input.padding} ${CONFIG.input.borderRadius} ${CONFIG.input.backgroundColor} ${CONFIG.input.textColor} ${CONFIG.input.placeholder} ${CONFIG.input.focus}`}
                  type="text"
                  name="username"
                  placeholder="Enter your username"
                  required
              />
              <input
                  className={`${CONFIG.input.width} ${CONFIG.input.padding} ${CONFIG.input.borderRadius} ${CONFIG.input.backgroundColor} ${CONFIG.input.textColor} ${CONFIG.input.placeholder} ${CONFIG.input.focus}`}
                  type="password"
                  name="password"
                  placeholder="Enter your password"
                  required
              />
              <button
                  type="submit"
                  className={`${CONFIG.button.width} ${CONFIG.button.padding} ${CONFIG.button.borderRadius} ${CONFIG.button.backgroundColor} ${CONFIG.button.textColor} ${CONFIG.button.fontWeight} ${CONFIG.button.hover} ${CONFIG.button.transition}`}
              >
                Register
              </button>
            </form>
            {message && (
                <p className={`${CONFIG.message.margin} ${CONFIG.message.textAlign} ${messageColor}`}>{message}</p>
            )}
          </div>
        </div>

        <div className={`${CONFIG.header.width} ${CONFIG.header.height}`}>
          <Footer />
        </div>
      </div>
  );
}

export default Register;