import React from 'react'
import '../../sass/Capture/Capture.scss'
import CaptureTable from './CaptureTable/CaptureTable'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
function Capture() {
  return (
    <div className='Capture'>
      <CaptureTable />
      <ToastContainer></ToastContainer>
    </div>
  )
}

export default Capture