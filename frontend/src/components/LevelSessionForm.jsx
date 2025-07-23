// import React, { useState } from 'react';
// import { motion } from 'framer-motion';
// import { Send } from 'lucide-react';
// import { createLevelSession } from '../services/apiService'; // Ensure this import is present
//
// const LevelSessionForm = ({ levels, accountId, onSessionCreated }) => {
//     const [levelId, setLevelId] = useState('');
//     const [cpm, setCpm] = useState('');
//     const [accuracy, setAccuracy] = useState('');
//     const [error, setError] = useState(null);
//
//     const handleSubmit = async e => {
//         e.preventDefault();
//         setError(null);
//
//         const sessionData = {
//             levelId,
//             accountId,
//             cpm: parseFloat(cpm),
//             accuracy: parseFloat(accuracy) / 100, // Convert percentage to decimal
//             startTime: new Date().toISOString(),
//             endTime: new Date().toISOString(),
//         };
//
//         try {
//             await createLevelSession(sessionData);
//             onSessionCreated();
//             setCpm('');
//             setAccuracy('');
//             setLevelId('');
//         } catch (err) {
//             setError('Failed to create session. Please try again.');
//         }
//     };
//
//     return (
//         <section className="mb-6">
//             <h2 className="text-2xl font-semibold mb-2 flex items-center">
//                 <Send className="mr-2" /> Start New Level Session
//             </h2>
//             <motion.form
//                 onSubmit={handleSubmit}
//                 className="border rounded-lg p-4 shadow-sm"
//                 initial={{ opacity: 0 }}
//                 animate={{ opacity: 1 }}
//                 transition={{ duration: 0.3 }}
//             >
//                 <div className="mb-4">
//                     <label className="block text-sm font-medium mb-1">Select Level</label>
//                     <select
//                         value={levelId}
//                         onChange={e => setLevelId(e.target.value)}
//                         className="w-full border rounded p-2"
//                         required
//                     >
//                         <option value="">Choose a level</option>
//                         {levels.map(level => (
//                             <option key={level.id} value={level.id}>
//                                 {level.language} - {level.difficulty}
//                             </option>
//                         ))}
//                     </select>
//                 </div>
//                 <div className="mb-4">
//                     <label className="block text-sm font-medium mb-1">CPM (Characters Per Minute)</label>
//                     <input
//                         type="number"
//                         value={cpm}
//                         onChange={e => setCpm(e.target.value)}
//                         className="w-full border rounded p-2"
//                         required
//                         min="0"
//                     />
//                 </div>
//                 <div className="mb-4">
//                     <label className="block text-sm font-medium mb-1">Accuracy (%)</label>
//                     <input
//                         type="number"
//                         value={accuracy}
//                         onChange={e => setAccuracy(e.target.value)}
//                         className="w-full border rounded p-2"
//                         required
//                         min="0"
//                         max="100"
//                         step="0.01"
//                     />
//                 </div>
//                 {error && <p className="text-red-500 mb-4">{error}</p>}
//                 <button
//                     type="submit"
//                     className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 flex items-center"
//                 >
//                     <Send className="mr-2" size={16} /> Submit Session
//                 </button>
//             </motion.form>
//         </section>
//     );
// };
//
// export default LevelSessionForm;