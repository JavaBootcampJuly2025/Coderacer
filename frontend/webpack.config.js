module.exports = {
  entry: {
    app: './src/index.js',
    admin: './src/admin.js'
  },
  output: {
    filename: '[name].bundle.js', // outputs app.bundle.js and admin.bundle.js
    path: path.resolve(__dirname, 'dist'),
  },
  // rest of config
};
