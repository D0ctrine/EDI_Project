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
  update (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/update', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  selectSQL (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/sql', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  saveSQL (detail) {
    return new Promise((resolve, reject) => {
      axios.post('/setting/bigQuery', detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  getSQL (detail) {
    return new Promise((resolve, reject) => {
      axios.get('/setting/getMainQuery?categoryId=' + detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  updateFileSendFlag (detail) {
    return new Promise((resolve, reject) => {
      axios.get('/fileDefine/statusChange?fileDefineId=' + detail.fileDefineId + '&flag=' + detail.flag).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  setDaemonFileSave (detail) {
    return new Promise((resolve, reject) => {
      axios.get('http://12.230.58.89:3000/makeFile/FILE/' + detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        console.log(error)
      })
    })
  },
  setDaemonSendFtp (detail) {
    return new Promise((resolve, reject) => {
      axios.get('http://12.230.58.89:3000/makeFile/FTP/' + detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        console.log(error)
      })
    })
  },
  copySetting (detail) {
    return new Promise((resolve, reject) => {
      axios.get('/copySetting?categoryId=' + detail).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  }
}
