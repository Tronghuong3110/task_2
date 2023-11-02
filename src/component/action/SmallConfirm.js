import { React, useState, useEffect, useContext } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/smallconfirm.scss'
const SmallConfirm = (props) => {
    const {setOpenConfirmScreen,action, handleFunction} = props;
    const handleCancel = ()=>{
        setOpenConfirmScreen(false)
    }
    const handleOk =()=>{
        setOpenConfirmScreen(false)
        handleFunction(action.module,action.action)
    }
    return (
        <div className='smallConfirmScreen'>
            <div className="smallConfirm">
                <div className="question">Are you sure to do this task with module</div>
                <div className="question"><strong>Kiểm tra đường truyền mạng 2</strong> ? </div>
                <div className='note'>Performing this operation does not affect other processes</div>
                <div className="confirmBtn">
                    <button className="confirmBtn-cancel" onClick={()=>{
                        handleCancel()
                    }}>Cancel</button>
                    <button className="confirmBtn-ok" onClick={()=>{
                        handleOk()
                    }}>Confirm</button>
                </div>
            </div>
        </div>
    )
}
export default SmallConfirm;