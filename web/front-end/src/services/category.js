import axios from 'axios'
import errorParser from '@/utils/error-parser'

export default {
  /**
   * Get the List from Categories in customers
   * @param {Object}
   */
  categories () {
    return new Promise((resolve, reject) => {
      axios.get('/categories').then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  create (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/categories/create', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  delete (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/categories/delete', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  update (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/categories/update', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  }
}
