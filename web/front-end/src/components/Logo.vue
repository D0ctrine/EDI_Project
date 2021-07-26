<template>
  <div class="logo-wrapper">
    <div style="font-size:30px;font: bolder;cursor:pointer;">
      <font-awesome-icon icon="file-alt" size="lg" style="color: red;"/>&nbsp;
    EDI</div>
  </div>
</template>

<script>
export default {
  name: 'Logo'
}
</script>

<style lang="scss" scoped>
.logo-wrapper {
  text-align: center;

  .tagline {
    line-height: 180%;
    color: #666;
  }

  .logo {
    max-width: 150px;
    margin: 0 auto;
  }
  .file-alt{
    color: red;
  }
}
</style>
