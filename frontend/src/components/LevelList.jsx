import React from 'react';
import { motion } from 'framer-motion';
import { Code } from 'lucide-react';

const LevelList = ({ levels }) => {
    return (
        <section className="mb-6">
            <h2 className="text-2xl font-semibold mb-2 flex items-center">
                <Code className="mr-2" /> Available Levels
            </h2>
            {levels.length === 0 ? (
                <p className="text-gray-500">No levels available.</p>
            ) : (
                <ul className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {levels.map(level => (
                        <motion.li
                            key={level.id}
                            className="border rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow"
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ duration: 0.3 }}
                        >
                            <h3 className="text-lg font-medium">{level.language} - {level.difficulty}</h3>
                            <p className="text-sm text-gray-600">Tags: {level.tags.join(', ')}</p>
                            <pre className="mt-2 bg-gray-100 p-2 rounded text-sm overflow-auto">
                {level.codeSnippet.substring(0, 100)}...
              </pre>
                        </motion.li>
                    ))}
                </ul>
            )}
        </section>
    );
};

export default LevelList;