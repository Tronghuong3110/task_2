import React from 'react'
import '../../sass/Capture/Capture.scss'
import CaptureTable from './CaptureTable/CaptureTable'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useParams } from 'react-router-dom';
function Capture() {
  const {ipServer} = useParams()
  return (
    <div className='Capture'>
      <CaptureTable ipServer={ipServer} />
      <ToastContainer></ToastContainer>
    </div>
  )
}

export default Capture