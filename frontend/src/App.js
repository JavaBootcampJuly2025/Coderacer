import React, { useEffect, useState } from 'react';
import { getAllAccounts } from './services/apiService';

function App() {
  const [accounts, setAccounts] = useState([]);

  useEffect(() => {
    getAllAccounts().then(data => setAccounts(data));
  }, []);

  return (
      <div>
        <h1>Accounts</h1>
        <ul>
          {accounts.map(account => (
              <li key={account.id}>{account.username} ({account.email})</li>
          ))}
        </ul>
      </div>
  );
}

export default App;