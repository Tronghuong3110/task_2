import React, { useState } from 'react'
import { Checkbox } from '@mui/material';
import { lightGreen } from '@mui/material/colors';
import { IP } from '../../Layout/constaints';
import SmallConfirm from '../../action/SmallConfirm';
const MonitorCheckbox = ({idInterface,idProbe,initMonitor}) => {
    const [monitor,setMonitor] = useState(initMonitor)
    const [isOpenConfirmWindow,setOpenConfirmScreen] = useState(false)
    const action = {
        action: 'change monitor status',
        module : [idInterface]
    }
    const handleOnChange = (e)=>{
        setOpenConfirmScreen(true)
    }
    const updateMonitorStatus = async ()=>{
        setMonitor(!monitor)
        let data = {
            id: idInterface,
            idProbe: idProbe,
            monitor:!monitor===true?1:0
        }
        let option = {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }
        await fetch(IP+"/api/v1/interface/update",option)
            .then(res => res.json())
            .then(data=> console.log(data))
            .catch(err => console.log(err))
    }
    return (
        <React.Fragment>
            <Checkbox
                sx={{
                    padding: '3px',
                    color: lightGreen["A400"],
                    '&.Mui-checked': {
                        color: lightGreen["A400"],
                    }
                }}
                checked={monitor}
                onChange={handleOnChange}
    
            />
            {isOpenConfirmWindow && <SmallConfirm setOpenConfirmScreen={setOpenConfirmScreen} action={action} object="interface" handleFunction={updateMonitorStatus}  />}
        </React.Fragment>
    )
}

export default MonitorCheckbox