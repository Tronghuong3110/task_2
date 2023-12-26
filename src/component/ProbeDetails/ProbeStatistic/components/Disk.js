import React from 'react'
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHardDrive } from '@fortawesome/free-solid-svg-icons';
import '../../../../sass/ProbeDetails/ProbeStatistic/components/Disk.scss'
function Disk(props) {
    const {disk} = props
    const setColorForUsage =(disk)=>{
        let percent = 100 - disk.percent.toFixed(2)*100
        if(percent<40) return "#00ff1a"
        else if(percent <80) return "#ffd233"
        else return "red"
    }
    return (
        <div className='disk d-flex' style={{marginRight:"15%", marginBottom:"16px"}}>
            <div className='disk-symbol'>
                <FontAwesomeIcon icon={faHardDrive}></FontAwesomeIcon>
            </div>
            <div className='disk-info'>
                <div className='disk-info-name'>{disk.disk_name}</div>
                <div className='disk-info-usage'>
                    <div className='usageBar'>
                        <div className='percent' style={{ width: `${100 - disk.percent.toFixed(2)*100}%`, backgroundColor: setColorForUsage(disk) }}> </div>
                    </div>
                </div>
                <div className='disk-info-note'>{disk.memory_free}G Free of {disk.total_memory}G </div>
            </div>
        </div>
    )
}
//** @import  */
export default Disk