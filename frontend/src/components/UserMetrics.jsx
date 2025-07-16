import React from 'react';
import { motion } from 'framer-motion';
import { BarChart2 } from 'lucide-react';

const UserMetrics = ({ metrics }) => {
    return (
        <section className="mb-6">
            <h2 className="text-2xl font-semibold mb-2 flex items-center">
                <BarChart2 className="mr-2" /> User Metrics
            </h2>
            <motion.div
                className="border rounded-lg p-4 shadow-sm"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3 }}
            >
                <p className="text-lg">
                    Average CPM: <span className="font-bold">{metrics.avgCpm.toFixed(2)}</span>
                </p>
                <p className="text-lg">
                    Average Accuracy: <span className="font-bold">{(metrics.avgAccuracy * 100).toFixed(2)}%</span>
                </p>
            </motion.div>
        </section>
    );
};

export default UserMetrics;