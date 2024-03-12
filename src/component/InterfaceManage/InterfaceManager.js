import React from 'react'
import InterfaceTable from './InterfaceTable/InterfaceTable'
import '../../sass/InterfaceManage/InterfaceManager.scss'
import { useParams } from 'react-router-dom'
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer } from 'react-toastify';
function InterfaceManager() {
  const {id} = useParams()
  return (
    <div className='InterfaceManager'>
        <h5 style={{color:'white'}}>Interface list of Probe</h5>
        <InterfaceTable id={id} />
        <ToastContainer />
    </div>
  )
}

export default InterfaceManager