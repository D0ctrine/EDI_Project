<template>
  <section class="ma-0 pa-4 text-center">
    <br>
    <h4>주간발송현황</h4>
    <line-chart style="height: 300px;width: 100%;align: center;"></line-chart>
    <v-spacer style="height: 30px;"></v-spacer>
    <v-divider></v-divider>
    <v-spacer style="height: 30px;"></v-spacer>
    <h4>금일 전송 로그</h4>
    <v-data-table
    :headers="headers"
    :items="desserts"
    :items-per-page="5"
    class="elevation-1 pt-1"
    dark
  >
  </v-data-table>
  </section>
</template>

<script>
import LineChart from '@/components/lineChart.js'

export default {
  name: 'VueChartJS',
  data () {
    return {
      headers: [
        {
          text: 'No',
          value: 'no',
          align: 'start',
          sortable: false
        },
        { text: '고객사', value: 'customer' },
        { text: '파일', value: 'filename' },
        { text: '발송시각', value: 'time' },
        { text: '발송여부', value: 'flag' },
        { text: '비고', value: 'note' },
        { text: '파일', value: 'file' }
      ],
      desserts: [
        {
          no: '1.',
          customer: 'Samsung',
          filename: 'SE_TSLDC.TXT',
          time: '2021-03-15 08:21:31',
          flag: 'N',
          note: '500서버 에러 발생',
          file: 'SE_TSLDC.TXT'
        },
        {
          no: '2.',
          customer: 'Hynix',
          filename: 'HX_TSLDC.TXT',
          time: '2021-03-15 09:22:31',
          flag: 'Y',
          note: '',
          file: 'HX_TSLDC.TXT'
        }
      ]
    }
  },
  components: {
    LineChart
  }
}
</script>

<style scoped>
  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  a {
    color: #42b983;
  }
</style>
