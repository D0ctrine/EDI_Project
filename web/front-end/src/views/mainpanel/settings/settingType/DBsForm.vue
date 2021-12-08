<template>
  <v-container id="container" fluid>
    <v-row align="center" style="width:94%;">
      <v-col
        class="d-flex"
        cols="10"
        sm="3"
      >
        <v-select
          :items="items"
          item-text="name"
          item-value="value"
          v-model="selectedItemValue"
          label="DB Type"
          dense
          outlined
        ></v-select>
      </v-col>
      <v-col align="right">
        <v-btn class="mr-10 purple lighten-2" tile v-on:click="daemonFileSave()">
          <v-icon left size="30">
            mdi-file-download
          </v-icon>
            Make File
        </v-btn>

        <v-btn class="mr-10 teal lighten-2" tile v-on:click="daemonFileSaveSendFTP()">
          <v-icon left size="30">
            mdi-file-export
          </v-icon>
            Send FTP
        </v-btn>
        <v-btn tile color="cyan" v-on:click="showResult()">
          <v-icon left size="30">
            mdi-database-search
          </v-icon>
            DATA
        </v-btn>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <v-btn tile color="default" v-on:click="saveData()" >
          <v-icon left size="30">
          mdi-content-save
        </v-icon>
            Save
        </v-btn>
      </v-col>
      <v-progress-linear style="margin-top: -20px;" class="mb-6" indeterminate v-if="this.lineProcess" color="yellow darken-2"></v-progress-linear>
      </v-row>
      <v-row>
        <span>
  <h5 class="text-left" style="float: left;">{{ selectedItemValue }} DB</h5>
  <h6 style="float: right;color: red;">{{ modifiedAlarm }}</h6>
    <div class="codemirror">
      <codemirror ref="myCm"
                  v-model="formData.queryText"
                  :options="cmOptions"
                  @ready="onCmReady"
                  @focus="onCmFocus"
                  @input="onCmCodeChange"
                  style="text-align: left;">
      </codemirror>
    </div>
        </span>
    </v-row>
    <v-snackbar
          v-model="snackbar"
        >
          {{ alertText }}
          <template v-slot:action="{ attrs }">
            <v-btn
              color="pink"
              text
              v-bind="attrs"
              @click="snackbar = false"
            >
              Close
            </v-btn>
          </template>
        </v-snackbar>
  </v-container>
</template>
<script>
import 'codemirror/mode/sql/sql.js'
import 'codemirror/theme/solarized.css'
import 'codemirror/addon/selection/active-line.js'
import 'codemirror/addon/edit/closebrackets.js'
import 'codemirror/mode/clike/clike.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/comment/comment.js'
import 'codemirror/addon/dialog/dialog.js'
import 'codemirror/addon/dialog/dialog.css'
import 'codemirror/addon/search/searchcursor.js'
import 'codemirror/addon/search/search.js'
import 'codemirror/keymap/emacs.js'
import settingService from '@/services/settings'
import configService from '@/services/config'
import FileSaver from 'file-saver'
import convert from 'xml-js'

export default {
  data () {
    return {
      lineProcess: false,
      snackbar: false,
      content: '',
      alertText: '',
      formData: {
        queryText: '',
        orginalQueryText: ''
      },
      selectedItemValue: '',
      modifiedAlarm: '',
      modifiedTime: '',
      cmOptions: {
        tabSize: 4,
        styleActiveLine: true,
        lineNumbers: true,
        line: true,
        mode: 'text/x-sql',
        theme: 'solarized light',
        lineWrapping: true
      },
      items: [
        { name: 'Main', value: 'MAIN' },
        { name: 'Mes', value: 'MES' },
        { name: 'Report', value: 'REPORT' }
      ]
    }
  },
  props: ['selected', 'settingPropsList'],
  created () {
    this.lineProcess = true
    settingService.getSQL(this.selected.id).then((returnList) => {
      this.formData.queryText = returnList.MainQuery.query
      this.formData.orginalQueryText = returnList.MainQuery.query
      this.selectedItemValue = returnList.MainQuery.dbtype
      this.lineProcess = false
    }).catch(error => {
      this.errorMessage = error.message
      this.snackbar = true
      this.alertText = this.errorMessage
      this.lineProcess = false
    })
  },
  computed: {
    codemirror () {
      return this.$refs.myCm.codemirror
    }
  },
  methods: {
    showSQL (val) {
      if (this.formData.queryText === val) {
        return val
      } else {
        return ''
      }
    },
    onCmReady (cm) {
      this.codemirror.setSize('100%', '560')
    },
    onCmFocus (cm) {
      console.log('the editor is focus!')
    },
    onCmCodeChange (newCode) {
      if (this.formData.orginalQueryText !== newCode) {
        if (this.modifiedTime === '') {
          let today = new Date()
          this.modifiedTime = today.toLocaleString()
          this.modifiedAlarm = this.modifiedTime + '  변경 시작됨(저장 필요!)'
        }
      } else {
        this.modifiedAlarm = ''
        this.modifiedTime = ''
      }
      this.formData.queryText = newCode
      this.$emit('codeChange', this.formData.queryText)
    },
    refreshData () {
      this.lineProcess = true
      settingService.getSQL(this.selected.id).then((returnList) => {
        this.formData.queryText = returnList.MainQuery.query
        this.formData.orginalQueryText = returnList.MainQuery.query
        this.selectedItemValue = returnList.MainQuery.dbtype
        this.lineProcess = false
      }).catch(error => {
        this.errorMessage = error.message
        console.log(this.errorMessage)
        this.lineProcess = false
      })
    },
    async saveData () {
      this.lineProcess = true
      // 마지막에 Parameter가 있을떄 마지막 공백있는지 확인하고 없으면 넣기
      await settingService.saveSQL({ query: this.formData.queryText, type: 'Main', settingId: this.selected.id, dbtype: this.selectedItemValue }).then(() => {
        this.formData.orginalQueryText = this.formData.queryText
        this.modifiedAlarm = ''
        this.modifiedTime = ''
        this.snackbar = true
        this.alertText = '저장되었습니다!'
        this.lineProcess = false
      }).catch(error => {
        this.snackbar = true
        this.alertText = error.message
        this.lineProcess = false
      })
    },
    async showResult () {
      this.lineProcess = true
      let itemBox = await []
      let itemQuery = await ''
      // Common에 있는 Item List 가져오기
      const elementId = this.selected.parent.parent.children.find(item => item.name === 'common')
      let uniqueItemList = await configService.getItems({ categoryId: elementId.id, exConfigId: this.selected.id }).then(response => {
        return [...this.settingPropsList.itemList, ...response.itemList]
      })
      for (const item of uniqueItemList) {
        if (item.type === 'DB') {
          console.log(item)
          itemQuery = await settingService.selectSQL({ query: item.query, database: item.dbtype })
        }
        if (itemQuery !== null && itemQuery !== undefined && item.type === 'DB') {
          await itemBox.push({ key: item.key, query: itemQuery.returnList[0].VALUE })
        } else if (item.type !== 'DB') {
          await itemBox.push({ key: item.key, query: item.query })
        }
      }
      await itemBox.sort(function (a, b) { return b.key.length - a.key.length })
      let changeFileName = this.settingPropsList.fileName
      for (const item of itemBox) {
        changeFileName = await changeFileName.replace(new RegExp(':' + item.key, 'g'), item.query)
      }
      let result = await settingService.selectSQL({ query: (this.formData.queryText + ' '), database: this.selectedItemValue, items: itemBox })
      const header = await this.settingPropsList.headerList
      const tail = await this.settingPropsList.tailList
      let start = await this.settingPropsList.envList.find(env => env.item === 'start').value
      let end = await this.settingPropsList.envList.find(env => env.item === 'end').value
      let between = await this.settingPropsList.envList.find(env => env.item === 'between').value
      start = start || ''
      end = end || ''
      between = between || ''
      this.content = ''
      for (const element of header) {
        if (element.data_type.toString() === 'h_sql') {
          this.content += await settingService.selectSQL({ query: element.value.toString(), database: 'REPORT' }).then((sqlList) => sqlList.returnList[0].VALUE).catch(error => { console.log(error.message) })
        } else {
          this.content += await element.value
        }
        // tail 1,2,3,,,마다 줄 바꿈
        this.content += await '\r\n'
      }
      console.log('start end between')
      console.log(start + '/' + end + '/' + between)
      if (this.settingPropsList.dataType === 'xml') {
        console.log('Its XML !=======')
        let parent = await this.settingPropsList.envList.find(env => env.item === 'parent').value
        let flatArrayJson = {}
        for (const element of result.returnList) {
          flatArrayJson[parent] = { _attributes: element }
          this.content += await convert.json2xml(flatArrayJson, { compact: true, ignoreComment: true, spaces: 4 })
          this.content += '\r\n'
        }
      } else if (this.settingPropsList.dataType === 'text') {
        for (const element of result.returnList) {
          this.content += await start
          for (const [, value] of Object.entries(element)) {
            this.content += await `${value}` + between
          }
          this.content.slice(-(between.length - 1))
          this.content += end.value + '\r\n'
        }
      } else {
      }
      await tail.forEach(element => {
        if (element.data_type.toString() === 't_sql') {
          settingService.selectSQL({ query: element.value.toString(), database: 'REPORT' }).then((sqlList) => {
            console.log('sqlList ======')
            console.log(sqlList)
            sqlList.forEach(element => {
              for (const [, value] of Object.entries(element)) {
                this.content += `${value} ` + between
              }
            })
          }).catch(error => { console.log(error.message) })
        } else {
          this.content += element.value
        }
        // header 1,2,3,,,마다 줄 바꿈
        this.content += '\r\n'
      })
      let blob = await new Blob([this.content], { type: 'text/plain;charset=' + this.settingPropsList.charSet })
      await FileSaver.saveAs(blob, changeFileName + '.' + this.settingPropsList.fileType)
    },
    async daemonFileSave () {
      this.lineProcess = true
      await settingService.setDaemonFileSave(this.selected.id).then(() => {
        this.snackbar = true
        this.alertText = '파일이 서버에 저장되었습니다!'
        this.lineProcess = false
      }).catch(error => {
        this.snackbar = true
        this.alertText = error.message
        this.lineProcess = false
      })
    },
    async daemonFileSaveSendFTP () {
      this.lineProcess = true
      await settingService.setDaemonSendFtp(this.selected.id).then(() => {
        this.snackbar = true
        this.alertText = '파일이 서버에 저장 후 FTP 전송되었습니다!'
        this.lineProcess = false
      }).catch(error => {
        this.snackbar = true
        this.alertText = error.message
        this.lineProcess = false
      })
    }
  }

}
</script>
