module.exports = {
  devServer: {
    port: 3000,
    proxy: {
      '/api/*': {
        target: 'https://localhost:8080'
      }
    }
  },

  configureWebpack: {
    entry: {
      app: './src/main.js',
      style: [
        'bootstrap/dist/css/bootstrap.min.css'
      ]
    }
  },

  transpileDependencies: [
    'vuetify'
  ]
}
