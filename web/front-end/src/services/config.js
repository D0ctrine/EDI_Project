import axios from 'axios'
import errorParser from '@/utils/error-parser'

export default {
  /**
   * Get the List from Categories in customers
   * @param {Object}
   */
  config (detail) {
    return new Promise((resolve, reject) => {
      axios.get('/config?categoryId=' + detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  create (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/config/create', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  deleteEmail (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/config/email/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  deleteEnv (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/config/env/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  deleteFtp (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/config/ftp/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  deleteQuery (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/config/query/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  update (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/config/update', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  getFtp (detail) {
    return new Promise((resolve, reject) => {
      axios.get('/config/ftp?categoryId=' + detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  getItems (detail) {
    return new Promise((resolve, reject) => {
      axios.get('/config/item?categoryId=' + detail.categoryId + '&exConfigId=' + detail.exConfigId).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  }
}
