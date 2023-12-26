import React, { useState,useEffect } from 'react'
import Disk from './components/Disk'
import { IP } from '../../Layout/constaints'
import { Box } from '@mui/material'

function DiskContainer(props) {
  const {probeId} = props
  const [diskList, setDiskList]= useState([])
  useEffect(()=>{
    fetch("/api/v1/memories?probeId="+probeId)
    .then(response => response.json())
    .then(data => setDiskList(data))
    .catch(err => console.log(err))
  },[])


  return (
    <div style={{display:"flex",flexWrap:"wrap",position:"relative",height: "100%"}}>
        {
          diskList.length===0?(<Box sx={style.emptyDisplay}>There is no disk in this probe</Box>):(
            diskList.map(disk=>{
              return(
                <Disk disk={disk}></Disk>
              )
            })
          )
        }
    </div>
  )
}
/**  @type {import("@mui/material").SxProps} */
const style = {
  emptyDisplay:{
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: "translate(-50%,-50%)"
  }
}

export default DiskContainer