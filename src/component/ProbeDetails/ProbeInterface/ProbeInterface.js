import React, { useEffect, useState } from 'react'
import Interface from './Interface'
import 'bootstrap/dist/css/bootstrap.css';
import { IP } from '../../Layout/constaints';
const ProbeInterface = ({ id }) => {
    const [interfaceList, setInterfaceList] = useState([])
    useEffect(() => {
        fetch(IP + "/api/v1/interface/list?idProbe=" + id)
            .then(res => res.json())
            .then(data => setInterfaceList(data))
            .then(err => console.log(err))
        const interval = setInterval(() => {
            fetch(IP + "/api/v1/interface/list?idProbe=" + id)
                .then(res => res.json())
                .then(data => setInterfaceList(data))
                .then(err => console.log(err))
        }, 15000)
        return () => {
            clearInterval(interval)
        }
    }, [])

    return (
        <React.Fragment>
            <div style={{ width: '15%' }}>
                <div className='text-center' style={{ fontSize: '1.3em', marginBottom: '20px', fontWeight: '500' }}>STATUS</div>
                <div className='StatusNote d-flex justify-content-around align-items-center'>
                    <div>
                        <div>
                            <div style={{ height: '2em', width: '2em', borderRadius: '50%', backgroundImage: 'linear-gradient(135deg, #EDFF1C, #349911)' }}></div>
                        </div>
                        <div className='text-center' style={{ padding: '2px 0', fontWeight: '500' }}>UP</div>
                    </div>
                    <div>
                        <div className='d-flex justify-content-center'>
                            <div style={{ height: '2em', width: '2em', borderRadius: '50%', backgroundColor: '#c1c3cf' }}></div>
                        </div>
                        <div style={{ padding: '2px 0', fontWeight: '500' }}>DOWN</div>
                    </div>
                </div>
            </div>
            <div style={{ width: '85%' }}>
                <div className='text-center' style={{ fontSize: '1.3em', marginBottom: '20px', fontWeight: '500' }}>INTERFACE LIST</div>
                <div className='ProbeInterface d-flex'  >
                    {interfaceList.length !== 0 ? (interfaceList.map(itf => {
                        return <Interface name={itf.interfaceName} status={itf.status === 1 ? "up" : "down"} />
                    })) : ""}
                </div>
            </div>
        </React.Fragment>
    )
}

export default ProbeInterface