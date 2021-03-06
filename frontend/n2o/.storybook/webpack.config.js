// you can use this file to add your custom webpack plugins, loaders and anything you like.
// This is just the basic way to add additional webpack configurations.
// For more information refer the docs: https://storybook.js.org/configurations/custom-webpack-config

// IMPORTANT
// When you add this file, we won't add the default configurations which is similar
// to "React Create App". This only has babel loader to load JavaScript.
const path = require('path');

module.exports = {
  resolve: {
    alias: {
      N2oStorybook: path.resolve(__dirname, './'),
    }
  },
  plugins: [
    // your custom plugins
  ],
  module: {
    rules: [
      // add your custom rules.
      {
        test: /.(ttf|otf|eot|svg|woff(2)?)(\?[a-z0-9]+)?$/,
        use: [{
          loader: 'file-loader'
        }]
      },
      {
        test: /\.css$/,
        use: [
          {
            loader: 'style-loader',
          },
          {
            loader: 'css-loader',
            options: {
              importLoaders: true,
            },
          }
        ]
      },
      {
        test: /\.scss/,
        include: [
          path.resolve(__dirname, '../src/sass/n2o.scss'),
        ],
        use: [
          {
            loader: 'style-loader',
          },
          {
            loader: 'css-loader',
            options: {
              importLoaders: true,
            },
          },
          {
            loader: 'sass-loader',
            options: {
              sourceMap: true,
            },
          }]
      },
      {
        test: /\.(js|jsx)$/,
        enforce: 'pre',
        use: [
          {
            options: {
              eslintPath: require.resolve('eslint'),
              fix: true,
              useEslintrc: true,
              quiet: true,
            },
            loader: require.resolve('eslint-loader'),
          },
        ],
        include: path.resolve(__dirname, '../src'),
        exclude: [/[/\\\\]node_modules[/\\\\]/],
      },
    ],
  },
};
