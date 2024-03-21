import React from 'react'
import ethernet from '../../../assets/pic/ethernet.png'
import '../../../sass/ProbeDetails/ProbeInterface/Interface.scss'
const Interface = ({ name, status }) => {

    const setStatus = () => {
        if (status === 'up') {
            return { backgroundImage: 'linear-gradient(135deg, #EDFF1C, #349911)' };
        } else {
            return { backgroundColor: '#c1c3cf' };
        }
    };

    return (
        <div className='interface' style={{marginLeft: '7.5%'}} >
            <div className='interfaceImg' style={setStatus()}>
                <img src={ethernet} ></img>
            </div>
            <div className='interfaceName'>{name}</div>
        </div>
    )
}

export default Interface
