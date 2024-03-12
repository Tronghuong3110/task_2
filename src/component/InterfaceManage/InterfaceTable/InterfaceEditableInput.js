import React, { useState,useRef,useEffect } from 'react'
import '../../../sass/InterfaceManage/InterfaceTable/InterfaceEditableInput.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {} from '@fortawesome/free-regular-svg-icons'
import { faCheck, faPen } from '@fortawesome/free-solid-svg-icons'
import { IP } from '../../Layout/constaints';
import { notify } from '../../action/notifyFunction';
import SmallConfirm from '../../action/SmallConfirm';
const InterfaceEditableInput = ({idInterface,idProbe,initDescription}) => {
    const inputRef = useRef(null);
    const [description,setDescription] = useState(initDescription)
    const [status,setStatus] = useState(true)
    const [isOpenConfirmWindow,setOpenConfirmScreen] = useState(false)
    const action = {
        action: "change interface's description",
        module : [idInterface]
    }
    useEffect(() => {
      if (!status) {
        inputRef.current.focus();
      }
    }, [status]);
    const handleChangeStatus = ()=>{
        if(status===false) {
            setOpenConfirmScreen(true)
        }
        setStatus(!status)
    }

    const updateDescription = ()=>{
        let data = {
            id: idInterface,
            idProbe: idProbe,
            description:description
        }
        let option = {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }
        fetch(IP+"/api/v1/interface/update",option)
            .then(res => res.json())
            .then(data=> notify(data.message,data.code))
            .catch(err => console.log(err))
    }
    return (
        <React.Fragment>
            <div className='InterfaceEditableInput'>
                <input ref={inputRef} value={description} onChange={(e)=>{setDescription(e.target.value)}} disabled={status} autoComplete='false'></input>
                <button onClick={handleChangeStatus}>
                    {status === true? (<FontAwesomeIcon icon={faPen} style={{ color: "#59D5E0" }} ></FontAwesomeIcon>):
                    (<FontAwesomeIcon icon={faCheck} size='lg' style={{ color: "#76ff03" }} ></FontAwesomeIcon>)}
                </button>
            </div>
            {isOpenConfirmWindow && <SmallConfirm  setOpenConfirmScreen={setOpenConfirmScreen} action={action} object="interface" handleFunction={updateDescription} />}
        </React.Fragment>
    )
}

export default InterfaceEditableInput