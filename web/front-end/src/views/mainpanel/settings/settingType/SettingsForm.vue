<template>
<v-app>
  <v-container
        fluid
        class="pa-0"
      >
      <v-row>
        <v-col>
          <v-btn color="blue" class="mr-16 white--text" v-on:click="saveData()" style="float: right">
            Save<v-icon right dark> mdi-content-save-all</v-icon>
          </v-btn>
          <v-btn class="mr-4 white--text green lighten-2" v-on:click="copyData()" style="float: right">
            Copy<v-icon right dark> mdi-content-copy</v-icon>
          </v-btn>
          <div v-if="this.sendFlag === 'N'">
            <v-icon size="30" color="amber" style="float: left">mdi-pause</v-icon>
          <v-btn color="green" v-on:click="updateCronStatus('Y')" class="mr-4 mb-2 white--text" style="float: right">
            Run<v-icon right dark> mdi-play-circle-outline</v-icon>
          </v-btn>
          </div>
          <div v-if="this.sendFlag === 'Y'">
          <v-progress-circular indeterminate color='amber' style="float: left;"></v-progress-circular>
          <v-btn  color="red" v-on:click="updateCronStatus('N')" class="mr-4 mb-2 white--text" style="float: right">
            Stop<v-icon right dark> mdi-pause-circle-outline</v-icon>
          </v-btn>
          </div>
          <v-progress-linear indeterminate v-if="this.lineProcess" color="yellow darken-2"></v-progress-linear>
        </v-col>
      </v-row>
  </v-container>
  <v-container fluid style="height: 740px;overflow-y:auto;overflow-x: hidden;">
    <v-textarea
      clearable
      clear-icon="mdi-close-circle"
      :label="this.selected.name.toUpperCase() + ' 파일 설명(참조용)'"
      rows="2"
      style="font-size: 18px;"
      :placeholder="this.selected.name.toUpperCase() + ' 파일 내용을 간략히 작성하시오.'"
      v-model="fileDescription"
      class="pt-0"
    ></v-textarea>
    <v-spacer style="height: 50px;"></v-spacer>

    <v-row>
      <p class="itemHeader text-left"><v-icon size="30">mdi-file-star-outline</v-icon>{{ this.selected.name.toUpperCase() }} 파일 저장 방식</p>
    </v-row>
    <v-row>
      <v-text-field label="파일 저장 폴더" v-model="fileFolder" placeholder="파일 저장 폴더" :rules="rules"></v-text-field>
      <v-text-field label="파일 명" v-model="filename" placeholder="파일 명" :rules="rules"></v-text-field>
      <v-text-field label="파일 형식(. 제외)" v-model="filetype" placeholder="파일 형식" style="width: 10px;" :rules="rules" color="green"></v-text-field>&nbsp;&nbsp;
      <v-select
          :items="encodingList"
          item-text="name"
          item-value="value"
          label="인코딩(Charset)"
          v-model="charset"
          color="purple"
          dense
          outlined
          class="mt-3"
          style="width: 20px;"
        ></v-select>
    </v-row>
    <v-spacer style="height: 50px;"></v-spacer>
<v-row>
    <p class="itemHeader text-left"><v-icon size="30">mdi-file-replace</v-icon> FTP 설정</p>
</v-row>
<v-row>
    <v-select
      style="font-size: 17px;"
      v-model="ftpItemId"
      :items="configFtpList"
      :item-text='(obj) => "Addr: " + (obj)["ftp_addr"] + " | User: " + (obj)["user_id"] + " | BaseDir: " + (obj)["base_dir"] + " | Type: " + (obj)["type"]'
      :item-value='(obj) => (obj)["id"].toString()'
    ></v-select>
</v-row>
    <v-spacer style="height: 50px;"></v-spacer>

        <v-row><p class="itemHeader text-left"><v-icon size="30">mdi-calendar-clock</v-icon> 주기(Crontab)</p></v-row>
          <Grid ref="cronGrid" :data="gridCronProps.data" :columns="gridCronProps.columns" :options="gridCronProps.options"/>
    <v-spacer style="height: 70px;"></v-spacer>
    <v-row>
          <p class="itemHeader text-left"><v-icon size="30">mdi-file-upload-outline</v-icon> 데이터 추출 방식</p>
    </v-row>
    <v-row>
          <v-radio-group style="float: left;" v-model="dataType" row dense>
            <v-radio label="Text" value="text"></v-radio>
            <v-radio label="XML" value="xml"></v-radio>
            <v-radio label="HTML" value="html"></v-radio>
          </v-radio-group>
          <v-checkbox
              style="float: right;"
              class="ml-15"
              :label="`No Data Send(${ noDataSendFlag=='true'?noDataSendFlag:'false' })`"
              v-model="noDataSendFlag"
              color="orange darken-3"
              value="true"
              hide-details
            ></v-checkbox>
    </v-row>
    <v-spacer style="height: 50px;"></v-spacer>

        <v-row><p class="itemHeader text-left"><v-icon size="30">mdi-cog-transfer-outline</v-icon> 환경(Env)
          <v-btn class="mx-2" v-on:click="insertRow('envGrid')" style="float: right;" fab small dark color="indigo">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
        </p></v-row>
      <Grid ref="envGrid" :data="gridEnvProps.data" :columns="gridEnvProps.columns" :options="gridEnvProps.options"/>

    <v-spacer style="height: 70px;"></v-spacer>

      <v-row><p class="text-left itemHeader"><v-icon size="30">mdi-page-layout-header</v-icon> Header
          <v-btn class="mx-2" v-on:click="insertRow('headGrid')" style="float: right;" fab small dark color="indigo">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
      </p></v-row>
        <Grid ref="headGrid" :data="gridHeadProps.data" :columns="gridHeadProps.columns" :options="gridHeadProps.options"/>

    <v-spacer style="height: 70px;"></v-spacer>

      <v-row><p class="text-left itemHeader"><v-icon size="30">mdi-page-layout-footer</v-icon> Tail
          <v-btn class="mx-2" v-on:click="insertRow('tailGrid')" style="float: right;" fab small dark color="indigo">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
      </p></v-row>
      <Grid ref="tailGrid" :data="gridTailProps.data" :columns="gridTailProps.columns" :options="gridTailProps.options"/>

    <v-spacer style="height: 70px;"></v-spacer>

      <v-row><p class="text-left itemHeader"><v-icon size="30">mdi-help-circle-outline</v-icon> 컬럼명(Convert)
          <v-btn class="mx-2" v-on:click="insertRow('itemGrid')" style="float: right;" fab small dark color="indigo">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
      </p></v-row>
        <Grid ref="itemGrid" :data="gridItemProps.data" :columns="gridItemProps.columns" :options="gridItemProps.options"/>
      <v-spacer style="height: 70px;"></v-spacer>
      <div class="text-center ma-2">
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
    </div>
  </v-container>
</v-app>
</template>
<script>
import 'tui-grid/dist/tui-grid.css'
import { Grid } from '@toast-ui/vue-grid'
import settingService from '@/services/settings'
import configService from '@/services/config'

class CustomDownBtnRenderer {
  constructor (props) {
    var el = document.createElement('button')
    el.innerHTML = '<span class="mdi mdi-close mdi-24px"></span>'
    el.style.width = '100%'
    el.style.backgroundColor = 'white'
    el.addEventListener('click', function () {
      const { grid, rowKey } = props
      var deleteRow = grid.getRow(rowKey)
      if (confirm((deleteRow.gridName === 'envGrid' ? '환경설정(Env)' : deleteRow.gridName === 'headGrid' ? 'Header' : deleteRow.gridName === 'tailGrid' ? 'Tail' : deleteRow.gridName === 'itemGrid' ? '컬럼명(Convert)' : '') + ' 값을 삭제하시겠습니까?')) {
        if (deleteRow.gridName === 'envGrid')settingService.deleteEnv(deleteRow)
        else if (deleteRow.gridName === 'headGrid' || deleteRow.gridName === 'tailGrid')settingService.deleteHeadNTail(deleteRow)
        else if (deleteRow.gridName === 'itemGrid')settingService.deleteQuery(deleteRow)
        grid.removeRow(rowKey)
      }
    })
    this.el = el
    this.render(props)
  }

  getElement () {
    return this.el
  }

  render (props) {
    this.el.value = String(props.value)
  }
}

const options1 = {
  scrollX: false,
  scrollY: false,
  height: 'auto',
  width: 'auto',
  rowHeight: 30,
  rowHeaders: ['rowNum'],
  header: {
    height: 30
  }
}
const headercols = [ // Env
  {
    header: 'Item',
    name: 'item',
    align: 'center',
    formatter: 'listItemText',
    editor: {
      type: 'select',
      options: {
        listItems: [
          { text: '(ROW)START', value: 'start' },
          { text: '(ROW)END', value: 'end' },
          { text: '(COL)BETWEEN', value: 'between' },
          { text: '(XML)PARENT', value: 'parent' }
        ]
      }
    }
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Note',
    name: 'note',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  }
]

const headercols2 = [ // Head
  {
    header: 'SEQ',
    name: 'id',
    hidden: true
  },
  {
    header: 'Type',
    name: 'data_type',
    align: 'center',
    formatter: 'listItemText',
    editor: {
      type: 'select',
      options: {
        listItems: [
          { text: 'Text', value: 'h_text' },
          { text: 'SQL', value: 'h_sql' }
        ]
      }
    }
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Note',
    name: 'cm_f02',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  }
]

const headercols3 = [ // Tail
  {
    header: 'SEQ',
    name: 'id',
    hidden: true
  },
  {
    header: 'Type',
    name: 'data_type',
    align: 'center',
    formatter: 'listItemText',
    editor: {
      type: 'select',
      options: {
        listItems: [
          { text: 'Text', value: 't_text' },
          { text: 'SQL', value: 't_sql' }
        ]
      }
    }
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Note',
    name: 'note',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  }
]

const headercols4 = [ // query
  {
    header: 'Alias',
    name: 'key',
    align: 'center',
    width: '160',
    editor: 'text'
  },
  {
    header: 'Query',
    name: 'query',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Type',
    name: 'type',
    width: '100',
    align: 'center',
    formatter: 'listItemText',
    editor: {
      type: 'select',
      options: {
        listItems: [
          { text: 'DB', value: 'DB' },
          { text: 'Text', value: 'TEXT' }
        ]
      }
    }
  },
  {
    header: 'DB-Type',
    name: 'dbtype',
    align: 'center',
    width: '100',
    formatter: 'listItemText',
    editor: {
      type: 'select',
      options: {
        listItems: [
          { text: 'Main', value: 'MAIN' },
          { text: 'Report', value: 'REPORT' },
          { text: 'Mes', value: 'MES' }
        ]
      }
    }
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  }
]

const headercols5 = [ // Cron
  {
    header: 'Cron Type',
    name: 'cronType',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  }
]

export default {
  name: 'myGrid',
  props: ['selected'],
  components: {
    Grid
  },
  async created () {
    this.getFileData()
  },
  data: () => ({
    rules: [
      value => !!value || 'Required.'
    ],
    snackbar: false,
    alertText: '',
    lineProcess: false,
    fileFolder: 'D:/',
    ftpItemId: '',
    filename: '',
    filetype: '',
    configFtpList: '',
    charset: '',
    cronData: [{ cronType: '월', value: '' }, { cronType: '주(일 0 ~ 토 6)', value: '' }, { cronType: '일', value: '' }, { cronType: '시', value: '' }, { cronType: '분', value: '' }],
    fileDescription: 'No Explanations',
    fileDescriptionID: 0,
    dataType: 'text',
    sendFlag: 'N',
    noDataSendFlag: 'false',
    gridEnvProps: { columns: headercols, options: options1 },
    gridHeadProps: { columns: headercols2, options: options1 },
    gridTailProps: { columns: headercols3, options: options1 },
    gridItemProps: { columns: headercols4, options: options1 },
    gridCronProps: { columns: headercols5, options: options1 },
    encodingList: [{ name: 'utf-8', value: 'utf-8' }, { name: 'ansi', value: 'latin1' }, { name: 'ascii', value: 'ascii' }, { name: 'euc-kr', value: 'euc-kr' }]
  }),
  methods: {
    getFileData () {
      this.lineProcess = true
      // Common에 있는 Ftp List 가져오기
      const elementId = this.selected.parent.parent.children.find(item => item.name === 'common')
      configService.getFtp(elementId.id).then(response => {
        this.configFtpList = response.ftpList
      })
      settingService.setting(this.selected.id)
        .then(async response => {
          if (response.filedefList) {
            console.log(response.filedefList)
            this.fileDescriptionID = await response.filedefList.id
            this.fileDescription = await response.filedefList.file_desc
            this.fileFolder = await response.filedefList.fileSaveFolder ? response.filedefList.fileSaveFolder : this.fileFolder
            this.noDataSendFlag = await response.filedefList.noDataSend
            this.sendFlag = await response.filedefList.send_flag
            this.filetype = await response.filedefList.fileType
            this.charset = await response.filedefList.fileCharset
            this.filename = await response.filedefList.fileName
            this.cronData = await [{ cronType: '월', value: response.filedefList.scheduleMonth }, { cronType: '주(일 0 ~ 토 6)', value: response.filedefList.scheduleWeek }, { cronType: '일', value: response.filedefList.scheduleDay }, { cronType: '시', value: response.filedefList.scheduleHour }, { cronType: '분', value: response.filedefList.scheduleMin }]
            this.ftpItemId = await response.filedefList.ftpEnvId
            console.log('ftpItemId')
            console.log(typeof this.ftpItemId)
            this.dataType = await response.filedefList.dataType
          } else {
            let fileDef = await { id: this.fileDescriptionID, schMonth: this.cronData[0].value, schWeek: this.cronData[1].value, schDay: this.cronData[2].value, schHour: this.cronData[3].value, schMin: this.cronData[4].value, fileDesc: this.fileDescription, dataType: this.dataType, fileName: this.filename, fileCharset: this.charset, fileType: this.filetype, noDataSend: this.noDataSendFlag, ftpEnvid: '', sendFlag: this.sendFlag }
            const fileSetting = await { env: [], fileDef: fileDef, headNtail: [], itemGrp: [], cg_id: this.selected.id }
            await settingService.create(fileSetting)
          }

          let headerResult = []
          this.gridHeadProps.data = await response.hntList.forEach(element => {
            if (element.data_type.startsWith('h')) {
              element.gridName = 'headGrid'
              headerResult.push(element)
            }
          })
          this.gridHeadProps.data = headerResult

          let tailResult = []
          await response.hntList.forEach(element => {
            if (element.data_type.startsWith('t')) {
              element.gridName = 'tailGrid'
              tailResult.push(element)
            }
          })
          this.gridTailProps.data = tailResult

          await response.envList.forEach((element) => { element.gridName = 'envGrid' })
          await response.itemList.forEach((element) => { element.gridName = 'itemGrid' })
          console.log('response.itemList')
          console.log(response.itemList)

          await this.$refs.envGrid.invoke('resetData', response.envList)
          await this.$refs.itemGrid.invoke('resetData', response.itemList)
          await this.$refs.tailGrid.invoke('resetData', this.gridTailProps.data)
          await this.$refs.headGrid.invoke('resetData', this.gridHeadProps.data)
          await this.$refs.cronGrid.invoke('resetData', this.cronData)
          this.lineProcess = false
        })
    },
    insertRow: function (event) {
      var rowData = { item: 'New Item', value: 'New Value', note: 'New Note' }
      let grid = ''
      console.log('event')
      console.log(event)
      if (event === 'envGrid') {
        rowData.gridName = 'envGrid'
        grid = this.$refs.envGrid
      } else if (event === 'headGrid') {
        rowData.gridName = 'headGrid'
        rowData.dataType = 'h_text'
        grid = this.$refs.headGrid
      } else if (event === 'tailGrid') {
        rowData.gridName = 'tailGrid'
        rowData.dataType = 't_text'
        grid = this.$refs.tailGrid
      } else if (event === 'itemGrid') {
        rowData.gridName = 'itemGrid'
        grid = this.$refs.itemGrid
      }

      const cnt = grid.invoke('getRowCount')
      console.log(rowData)
      grid.invoke('appendRow', rowData, { at: cnt, focus: true })
    },
    saveData: async function () {
      if (confirm('저장하시겠습니까?')) {
        let env = await this.$refs.envGrid.invoke('getModifiedRows').createdRows
        let head = await this.$refs.headGrid.invoke('getModifiedRows').createdRows
        let tail = await this.$refs.tailGrid.invoke('getModifiedRows').createdRows
        let headNtail = await [ ...head, ...tail ]
        let itemGrp = await this.$refs.itemGrid.invoke('getModifiedRows').createdRows
        let resultFlag = { create: '', update: '' }
        let resultMsg = { create: '', update: '' }
        const fileSetting = await {
          env: env,
          headNtail: headNtail,
          itemGrp: itemGrp,
          cg_id: this.selected.id + ''
        }

        await settingService.create(fileSetting).then(() => {
          resultFlag.create = true
        }).catch(error => {
          resultFlag.create = false
          resultMsg.create = error.message
        })
        let cronList = await this.$refs.cronGrid.invoke('getData')
        let fileDef = await { id: this.fileDescriptionID, schMonth: cronList[0].value, schWeek: cronList[1].value, schDay: cronList[2].value, schHour: cronList[3].value, schMin: cronList[4].value, fileDesc: this.fileDescription, fileSaveFolder: this.fileFolder, dataType: this.dataType, fileName: this.filename, fileCharset: this.charset, fileType: this.filetype, noDataSend: this.noDataSendFlag, ftpEnvid: this.ftpItemId, sendFlag: this.sendFlag }
        env = await this.$refs.envGrid.invoke('getModifiedRows').updatedRows
        head = await this.$refs.headGrid.invoke('getModifiedRows').updatedRows
        tail = await this.$refs.tailGrid.invoke('getModifiedRows').updatedRows
        headNtail = await [ ...head, ...tail ]
        itemGrp = await this.$refs.itemGrid.invoke('getModifiedRows').updatedRows
        console.log('update itemGrp')
        console.log(itemGrp)
        const updateSetting = await {
          env: env,
          headNtail: headNtail,
          fileDef: fileDef,
          itemGrp: itemGrp,
          cg_id: this.selected.id
        }
        await settingService.update(updateSetting).then(() => {
          resultFlag.update = true
        }).catch(error => {
          resultFlag.update = false
          resultMsg.update = error.message
        })
        this.snackbar = true
        if (resultFlag.create && resultFlag.update) {
          this.alertText = '데이터가 저장되었습니다!'
        } else {
          this.alertText = resultFlag.create ? resultMsg.update : resultMsg.create
        }

        await this.getFileData()
      }
    },
    copyData: function () {
      settingService.copySetting(this.selected.id).then(() => {
        this.snackbar = true
        this.alertText = '데이터가 복사되었습니다!'
        this.$emit('refreshCategory', true)
      })
    },
    sendDataToDBcomponent: function () {
      const propsList = {
        envList: this.$refs.envGrid.invoke('getData'),
        headerList: this.$refs.headGrid.invoke('getData'),
        tailList: this.$refs.tailGrid.invoke('getData'),
        itemList: this.$refs.itemGrid.invoke('getData'),
        dataType: this.dataType, // File Data Type ( Text,Xml,Html... )
        fileName: this.filename, // File Name
        charSet: this.charset, // File Encoding Type( Ansi,UTF-8,... )
        fileType: this.filetype // File Save Type ( txt,csv,pdf,... )
      }
      return propsList
    },
    updateCronStatus: function (flag) {
      let alertString = '스케줄링에 작업 등록 하시겠습니까?'
      if (flag !== 'Y') {
        flag = 'N'
        alertString = '실행 중지하시겠습니까?'
      }
      if (confirm(alertString)) {
        const sendParams = { fileDefineId: this.fileDescriptionID.toString(), flag: flag }
        settingService.updateFileSendFlag(sendParams).then(this.sendFlag = flag)
      }
    }
  }
}
</script>

<style>
  .v-label {
    font-size: 13px;
  }
  .itemHeader {
    font-weight: bold;
    width: 97%;
    font-size: 14px;
  }
</style>
