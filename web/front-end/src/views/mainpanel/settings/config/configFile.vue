<template>
<v-app>
  <h5 class="pt-3 pb-0 mb-0 green"><v-icon size="35">mdi-file-cog</v-icon>    File Common Settings</h5>
  <v-divider class="dark mt-1"></v-divider>
  <p>
  <v-btn color="blue-grey" class="white--text mr-16 mb-1" v-on:click="saveData()" style="float: right;">
      Save<v-icon right dark> mdi-content-save-all</v-icon>
    </v-btn>
    <v-progress-linear  class="mt-1" indeterminate v-if="this.lineProcess" color="yellow darken-2"></v-progress-linear>
    </p>
  <v-container style="height: 780px;overflow-y:auto;overflow-x: hidden;" fluid>
  <v-spacer style="height: 20px;flex-grow: 0 !important;"></v-spacer>
  <p class="text-left itemHeader" style="min-width: 600px;"><v-icon size="25">mdi-cog-box</v-icon> 환경(Env)
  <v-btn class="mx-2" v-on:click="insertRow('grid1')" style="float: right;" fab small dark color="indigo">
      <v-icon dark>
        mdi-plus
      </v-icon>
    </v-btn>
  </p>
  <grid ref="tuiGrid1" :data="envProps.data" :columns="envProps.columns" :options="envProps.options"/>

  <v-spacer style="height: 70px;flex-grow: 0 !important;"></v-spacer>
  <p class="text-left itemHeader" style="min-width: 600px;"><v-icon size="25">mdi-email</v-icon> 메일
  <v-btn class="mx-2" v-on:click="insertRow('grid2')" style="float: right;" fab small dark color="indigo">
      <v-icon dark>
        mdi-plus
      </v-icon>
    </v-btn>
  </p>
  <grid ref="tuiGrid2" :data="mailProps.data" :columns="mailProps.columns" :options="mailProps.options"/>

  <v-spacer style="height: 70px;flex-grow: 0 !important;"></v-spacer>
  <p class="text-left itemHeader" style="min-width: 600px;"><v-icon size="25">mdi-file-star</v-icon> FTP
  <v-btn class="mx-2" v-on:click="insertRow('grid3')" style="float: right;" fab small dark color="indigo">
      <v-icon dark>
        mdi-plus
      </v-icon>
    </v-btn>
  </p>
  <grid ref="tuiGrid3" :data="ftpProps.data" :columns="ftpProps.columns" :options="ftpProps.options"/>

  <v-spacer style="height: 70px;flex-grow: 0 !important;"></v-spacer>
  <p class="text-left itemHeader" style="min-width: 600px;"><v-icon size="25">mdi-database-arrow-right</v-icon> Value
  <v-btn class="mx-2" v-on:click="insertRow('grid4')" style="float: right;" fab small dark color="indigo">
      <v-icon dark>
        mdi-plus
      </v-icon>
    </v-btn>
  </p>
  <grid ref="tuiGrid4" :data="queryProps.data" :columns="queryProps.columns" :options="queryProps.options"/>
  <v-spacer style="height: 70px;flex-grow: 0 !important;"></v-spacer>
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
import configService from '@/services/config'

class CustomDownBtnRenderer {
  constructor (props) {
    var el = document.createElement('button')
    el.innerHTML = '<span class="mdi mdi-close mdi-24px"></span>'
    el.style.width = '100%'
    el.style.backgroundColor = 'white'
    el.addEventListener('click', function () {
      const { grid, rowKey, columnInfo } = props
      console.log('grid')
      console.log(grid)
      console.log('columnInfo')
      console.log(columnInfo)
      var deleteRow = grid.getRow(rowKey)
      console.log('deleteRow')
      console.log(deleteRow)
      if (confirm((deleteRow.gridName === 'grid1' ? '환경(Env)' : deleteRow.gridName === 'grid2' ? '메일' : deleteRow.gridName === 'grid3' ? 'FTP' : deleteRow.gridName === 'grid4' ? 'Value' : '') + ' 값을 삭제하시겠습니까?')) {
        if (deleteRow.gridName === 'grid1')configService.deleteEmail(deleteRow)
        else if (deleteRow.gridName === 'grid2')configService.deleteEnv(deleteRow)
        else if (deleteRow.gridName === 'grid3')configService.deleteFtp(deleteRow)
        else if (deleteRow.gridName === 'grid4')configService.deleteQuery(deleteRow)
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

const options = {
  scrollX: false,
  scrollY: false,
  height: 'auto',
  width: 'auto',
  rowHeight: 30,
  rowHeaders: ['rowNum'],
  header: {
    height: 30
  },
  columnOptions: {
    resizable: true
  }
}
const envCols = [
  {
    header: 'ID',
    name: 'id',
    hidden: true
  },
  {
    header: 'Item',
    name: 'item',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Comment',
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
  },
  {
    header: 'gridName',
    name: 'gridName',
    align: 'center',
    hidden: true
  },
  {
    header: 'set_type',
    name: 'set_type',
    align: 'center',
    hidden: true
  }
]

const mailCols = [
  {
    header: 'ID',
    name: 'id',
    hidden: true
  },
  {
    header: 'Host',
    name: 'host',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Port',
    name: 'port',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Mail-ID',
    name: 'access_id',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Mail-PW',
    name: 'access_pwd',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  },
  {
    header: 'gridName',
    name: 'gridName',
    align: 'center',
    hidden: true
  }
]

const ftpCols = [
  {
    header: 'ID',
    name: 'id',
    hidden: true
  },
  {
    header: 'Address',
    name: 'ftp_addr',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'FTP-User',
    name: 'user_id',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'FTP-PW',
    name: 'pwd',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Base Directory',
    name: 'base_dir',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Type',
    name: 'type',
    align: 'center',
    formatter: 'listItemText',
    editor: {
      type: 'select',
      options: {
        listItems: [
          { text: 'SFTP', value: 'SFTP' },
          { text: 'FTP', value: 'FTP' }
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
  },
  {
    header: 'gridName',
    name: 'gridName',
    align: 'center',
    hidden: true
  }
]
const queryCols = [
  {
    header: 'ID',
    name: 'id',
    hidden: true
  },
  {
    header: 'Alias',
    name: 'key',
    width: '160',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Value',
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
  },
  {
    header: 'gridName',
    name: 'gridName',
    align: 'center',
    hidden: true
  }
]

export default {
  name: 'myGrid',
  props: ['selected'],
  components: {
    Grid
  },
  data () {
    return {
      snackbar: false,
      alertText: '',
      lineProcess: true,
      envProps: { columns: envCols, options: options },
      mailProps: { columns: mailCols, options: options },
      ftpProps: { columns: ftpCols, options: options },
      queryProps: { columns: queryCols, options: options }
    }
  },
  created () {
    configService.config(this.selected.id).then((settingList) => {
      if (settingList.emailList.length) {
        for (let i = 0; i < settingList.emailList.length; i++) {
          settingList.emailList[i].gridName = 'grid1'
        }
      }
      if (settingList.envList.length) {
        for (let i = 0; i < settingList.envList.length; i++) {
          settingList.envList[i].gridName = 'grid2'
        }
      }
      if (settingList.ftpList.length) {
        for (let i = 0; i < settingList.ftpList.length; i++) {
          settingList.ftpList[i].gridName = 'grid3'
        }
      }
      if (settingList.itemList.length) {
        for (let i = 0; i < settingList.itemList.length; i++) {
          settingList.itemList[i].gridName = 'grid4'
        }
      }
      this.$refs.tuiGrid1.invoke('resetData', settingList.envList)
      this.$refs.tuiGrid2.invoke('resetData', settingList.emailList)
      this.$refs.tuiGrid3.invoke('resetData', settingList.ftpList)
      this.$refs.tuiGrid4.invoke('resetData', settingList.itemList)
    }).then(() => {
      this.lineProcess = false
    }).catch(error => {
      this.snackbar = true
      this.alertText = error.message
    })
  },
  methods: {
    insertRow: function (event) {
      var rowData = { item: 'New Item', value: 'New Value', note: 'New Note' }

      let grid = ''
      if (event === 'grid1') {
        rowData.gridName = 'grid1'
        rowData.set_type = 'COMMON'
        grid = this.$refs.tuiGrid1
      } else if (event === 'grid2') {
        rowData.gridName = 'grid2'
        grid = this.$refs.tuiGrid2
      } else if (event === 'grid3') {
        rowData.gridName = 'grid3'
        grid = this.$refs.tuiGrid3
      } else if (event === 'grid4') {
        rowData.gridName = 'grid4'
        grid = this.$refs.tuiGrid4
      }

      const cnt = grid.invoke('getRowCount')
      grid.invoke('appendRow', rowData, { at: cnt, focus: true })
    },
    saveData: async function () {
      if (confirm('저장하시겠습니까?')) {
        let env = this.$refs.tuiGrid1.invoke('getModifiedRows').createdRows
        let email = this.$refs.tuiGrid2.invoke('getModifiedRows').createdRows
        let ftp = this.$refs.tuiGrid3.invoke('getModifiedRows').createdRows
        let itemGrp = this.$refs.tuiGrid4.invoke('getModifiedRows').createdRows

        const config = {
          env: env,
          email: email,
          ftp: ftp,
          itemGrp: itemGrp,
          cg_id: this.selected.id
        }

        await configService.create(config)
        console.log('create itemGrp')
        console.log(itemGrp)

        env = this.$refs.tuiGrid1.invoke('getModifiedRows').updatedRows
        email = this.$refs.tuiGrid2.invoke('getModifiedRows').updatedRows
        ftp = this.$refs.tuiGrid3.invoke('getModifiedRows').updatedRows
        itemGrp = this.$refs.tuiGrid4.invoke('getModifiedRows').updatedRows

        const updateConfig = {
          updtEnv: env,
          updtEmail: email,
          updtFtp: ftp,
          updtItemGrp: itemGrp,
          cg_id: this.selected.id
        }

        await configService.update(updateConfig)

        await configService.config(this.selected.id).then((settingList) => {
          if (settingList.emailList.length) {
            for (let i = 0; i < settingList.emailList.length; i++) {
              settingList.emailList[i].gridName = 'grid1'
            }
          }
          if (settingList.envList.length) {
            for (let i = 0; i < settingList.envList.length; i++) {
              settingList.envList[i].gridName = 'grid2'
            }
          }
          if (settingList.ftpList.length) {
            for (let i = 0; i < settingList.ftpList.length; i++) {
              settingList.ftpList[i].gridName = 'grid3'
            }
          }
          if (settingList.itemList.length) {
            for (let i = 0; i < settingList.itemList.length; i++) {
              settingList.itemList[i].gridName = 'grid4'
            }
          }
          this.$refs.tuiGrid1.invoke('resetData', settingList.envList)
          this.$refs.tuiGrid2.invoke('resetData', settingList.emailList)
          this.$refs.tuiGrid3.invoke('resetData', settingList.ftpList)
          this.$refs.tuiGrid4.invoke('resetData', settingList.itemList)
        }).then(() => {
          this.snackbar = true
          this.alertText = '데이터가 저장되었습니다!'
        }).catch(error => {
          this.snackbar = true
          this.alertText = error.message
        })
      }
    }
  }
}
</script>
<style>
.itemHeader{
  font-size: 14px;
  font-weight: bold;
}
</style>
