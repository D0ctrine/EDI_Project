import axios from 'axios'
import errorParser from '@/utils/error-parser'

export default {
  /**
   * setting service for Out(Send) Files
   */
  setting (detail) {
    return new Promise((resolve, reject) => {
      axios.get('/setting?categoryId=' + detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  deleteFileDesc (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/filedesc/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  deleteHeadNTail (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/headntail/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  deleteEnv (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/env/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  deleteQuery (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/query/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  create (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/create', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  udpate (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/update', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  }
}
