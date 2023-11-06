import { createContext, useState, useEffect } from "react";
import { IP } from "../../Layout/constaints";

const ProbesContext = createContext()

const ProbesProvider = ({ children }) => {
    // const [openDeleteScreen,setOpenDeleteScreen] = useState(false)
    // const [deletedProbe,setDeletedProbe] = useState({})
    const [probes, setProbes] = useState([])
    const [conditions, setConditions] = useState({
        "name": null,
        "location": null,
        "area": null,
        "vlan": null
    })
    // function removeProbe(id){
    //     // alert("Xoa")
    //     const options = {
    //         method: 'DELETE',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         }
    //     };
    //     fetch("http://localhost:8081/api/v1/probe?id=" + id,options)
    //         .then(response => response.text())
    //         .then(data => {
    //             const newArray = probes.filter(item => item.id !== id);
    //             setProbes(newArray)
    //         })
    //         .catch(err => console.log(err))
    // }
    useEffect(() => {
        getProbes()
    }, [conditions])
    const getProbes = () => {
        let { name, location, area, vlan } = conditions
        let url = "http://" + IP + ":8081/api/v1/probes?" +
            (name ? "name=" + name : '') +
            (location ? "&location=" + location : '') +
            (area ? "&area=" + area : '') +
            (vlan ? "&vlan=" + vlan : '')

        fetch(url)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setProbes(data)
            })
            .catch(err => console.log(err))
    }
    const value = {
        probes,
        setProbes,
        getProbes,
        conditions,
        setConditions
    }

    return (
        <ProbesContext.Provider value={value}>
            {children}
        </ProbesContext.Provider>
    )
}

export { ProbesContext, ProbesProvider }